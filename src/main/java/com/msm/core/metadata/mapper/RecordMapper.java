package com.msm.core.metadata.mapper;

import com.msm.core.metadata.typesafe.DataRecord;
import com.msm.core.metadata.typesafe.TypedAttribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Declarative, metadata-driven mapper between DataRecords (a runtime counterpart of MapStruct).
 * <p>
 * A mapper is declared once as a constant and reused; it is immutable and thread-safe.
 * <pre>
 * static final RecordMapper CUSTOMER_TO_INVOICE_ACCOUNT = RecordMapper.builder()
 *         .map(CustomerIdReference.CODE, InvoiceAccountIdReference.CODE)
 *         .map(CustomerIdReference.ACCOUNT_NAME, InvoiceAccountIdReference.ACCOUNT_NAME, String::trim)
 *         .mapRef(CustomerIdReference.INVOICE_ACCOUNT_ID, InvoiceAccountIdReference.INVOICE_NAME, InvoiceAccountIdReference.INVOICE_NAME)
 *         .constant(InvoiceAccountIdReference.INVOICE_EMAIL, "n/a")
 *         .compute(InvoiceAccountIdReference.FULL_ADDRESS, src -> src.get(CustomerIdReference.FULL_ADDRESS))
 *         .build();
 *
 * Map&lt;String, Object&gt; ref = CUSTOMER_TO_INVOICE_ACCOUNT.map(customerRefMap);
 * </pre>
 * Rules:
 * <ul>
 *   <li>Only declared mappings are written; nothing is copied implicitly.</li>
 *   <li>A source field that is absent is skipped, so the target does not get a null key for it.</li>
 *   <li>A source field present with a null value writes null, unless {@link Builder#ignoreNulls()} is set.</li>
 *   <li>Converters are not called for null values; null passes through.</li>
 * </ul>
 */
public final class RecordMapper implements Function<DataRecord, DataRecord> {

    @FunctionalInterface
    private interface Step {
        void apply(DataRecord source, DataRecord target, RecordMapper mapper);
    }

    private final List<Step> steps;
    private final Set<String> ignored;
    private final boolean ignoreNulls;

    private RecordMapper(List<Step> steps, Set<String> ignored, boolean ignoreNulls) {
        this.steps = List.copyOf(steps);
        this.ignored = Set.copyOf(ignored);
        this.ignoreNulls = ignoreNulls;
    }

    public static Builder builder() {
        return new Builder();
    }

    // =========================================================
    // Execution
    // =========================================================

    /** Maps source into a new record. */
    public DataRecord map(DataRecord source) {
        return mapInto(source, DataRecord.of());
    }

    /** Maps source into a new map. A null source yields an empty map. */
    public Map<String, Object> map(Map<String, Object> source) {
        return map(DataRecord.ofNullable(source)).getValues();
    }

    /** Applies the mappings onto an existing target (MapStruct's @MappingTarget). */
    public DataRecord mapInto(DataRecord source, DataRecord target) {
        for (Step step : steps) {
            step.apply(source, target, this);
        }
        return target;
    }

    public List<DataRecord> mapAll(Collection<DataRecord> sources) {
        return sources.stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public DataRecord apply(DataRecord source) {
        return map(source);
    }

    private void write(DataRecord target, String fieldName, Object value) {
        if (ignored.contains(fieldName)) return;
        if (value == null && ignoreNulls) return;
        target.put(fieldName, value);
    }

    private <T> void write(DataRecord target, TypedAttribute<T> attr, T value) {
        if (ignored.contains(attr.getFieldName())) return;
        if (value == null && ignoreNulls) return;
        target.set(attr, value);
    }

    // =========================================================
    // Builder
    // =========================================================

    public static final class Builder {
        private final List<Step> steps = new ArrayList<>();
        private final Set<String> ignored = new HashSet<>();
        private boolean ignoreNulls;

        private Builder() {}

        /** source.field -> target.field, same type. */
        public <T> Builder map(TypedAttribute<T> source, TypedAttribute<T> target) {
            steps.add((src, tgt, mapper) -> {
                if (!src.contains(source)) return;
                mapper.write(tgt, target, src.get(source));
            });
            return this;
        }

        /** source.field -> converter -> target.field. The converter is skipped for null values. */
        public <S, T> Builder map(TypedAttribute<S> source, TypedAttribute<T> target, Function<? super S, ? extends T> converter) {
            steps.add((src, tgt, mapper) -> {
                if (!src.contains(source)) return;
                S value = src.get(source);
                mapper.write(tgt, target, value == null ? null : converter.apply(value));
            });
            return this;
        }

        /** source.container.inner -> target.field, where container is a Map attribute of source. */
        public <T> Builder mapNested(TypedAttribute<Map<String, Object>> container, TypedAttribute<T> inner, TypedAttribute<T> target) {
            steps.add((src, tgt, mapper) -> {
                DataRecord nested = DataRecord.ofNullable(src.get(container));
                if (!nested.contains(inner)) return;
                mapper.write(tgt, target, nested.get(inner));
            });
            return this;
        }

        /** source.&lt;refOwner&gt;IdReference.inner -> target.field (reads the JSON reference of refOwner). */
        public <T> Builder mapRef(TypedAttribute<?> refOwner, TypedAttribute<T> inner, TypedAttribute<T> target) {
            steps.add((src, tgt, mapper) -> {
                DataRecord ref = DataRecord.ofNullable(src.getRefAsMap(refOwner));
                if (!ref.contains(inner)) return;
                mapper.write(tgt, target, ref.get(inner));
            });
            return this;
        }

        /** Always writes a fixed value. */
        public <T> Builder constant(TypedAttribute<T> target, T value) {
            steps.add((src, tgt, mapper) -> mapper.write(tgt, target, value));
            return this;
        }

        /** Writes a value derived from the whole source record (MapStruct's expression). */
        public <T> Builder compute(TypedAttribute<T> target, Function<DataRecord, ? extends T> fn) {
            steps.add((src, tgt, mapper) -> mapper.write(tgt, target, fn.apply(src)));
            return this;
        }

        /** Copies the given fields by name, like {@link References#pick(Map, List)}. */
        public Builder copy(List<TypedAttribute<?>> fields) {
            steps.add((src, tgt, mapper) ->
                    References.pick(src.getValues(), fields).forEach((name, value) -> mapper.write(tgt, name, value)));
            return this;
        }

        /** Copies the given fields, reading renamed ones from sourceFields (target field -> source field). */
        public Builder copy(List<TypedAttribute<?>> fields, Map<TypedAttribute<?>, TypedAttribute<?>> sourceFields) {
            steps.add((src, tgt, mapper) ->
                    References.pick(src.getValues(), fields, sourceFields).forEach((name, value) -> mapper.write(tgt, name, value)));
            return this;
        }

        /** source.mapField -> nested mapper -> target.mapField. Skipped when the source map is null. */
        public Builder mapWith(TypedAttribute<Map<String, Object>> source, TypedAttribute<Map<String, Object>> target, RecordMapper nested) {
            steps.add((src, tgt, mapper) -> {
                Map<String, Object> value = src.get(source);
                if (value == null) return;
                mapper.write(tgt, target, nested.map(value));
            });
            return this;
        }

        /** nested mapper(source) -> target.&lt;refOwner&gt;IdReference (writes the JSON reference of refOwner). */
        public Builder refWith(TypedAttribute<?> refOwner, RecordMapper nested) {
            steps.add((src, tgt, mapper) -> tgt.setRef(refOwner, nested.map(src).getValues()));
            return this;
        }

        /**
         * Stores an entity reference on target following the reference rule
         * (id column, optional code column, JSON reference restricted to fields).
         *
         * @param sourceOf extracts the referenced entity's map from the source record
         */
        public Builder reference(TypedAttribute<UUID> idColumn,
                                 TypedAttribute<String> codeColumn,
                                 List<TypedAttribute<?>> fields,
                                 Function<DataRecord, Map<String, Object>> sourceOf) {
            steps.add((src, tgt, mapper) ->
                    References.setReference(tgt, idColumn, codeColumn, sourceOf.apply(src), fields));
            return this;
        }

        /** Same as {@link #reference(TypedAttribute, TypedAttribute, List, Function)} for a source whose keys differ. */
        public Builder reference(TypedAttribute<UUID> idColumn,
                                 TypedAttribute<String> codeColumn,
                                 List<TypedAttribute<?>> fields,
                                 Function<DataRecord, Map<String, Object>> sourceOf,
                                 Map<TypedAttribute<?>, TypedAttribute<?>> sourceFields) {
            steps.add((src, tgt, mapper) ->
                    References.setReference(tgt, idColumn, codeColumn, sourceOf.apply(src), fields, sourceFields));
            return this;
        }

        /** Target fields that are never written, whatever the mappings say. */
        public Builder ignore(TypedAttribute<?>... targets) {
            for (TypedAttribute<?> target : targets) {
                ignored.add(target.getFieldName());
            }
            return this;
        }

        /** Do not write null values; useful with {@link RecordMapper#mapInto} to keep existing target values. */
        public Builder ignoreNulls() {
            this.ignoreNulls = true;
            return this;
        }

        public RecordMapper build() {
            return new RecordMapper(steps, ignored, ignoreNulls);
        }
    }
}
