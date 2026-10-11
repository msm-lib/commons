package com.msm.core.metadata.mapper;

import com.msm.core.metadata.typesafe.DataRecord;
import com.msm.core.metadata.typesafe.TypedAttribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.msm.core.metadata.typesafe.MetaFieldBuilder.attr;

/**
 * Reference rule: a property referencing another entity stores
 * - its id (and code, when the entity has a code column) as real columns,
 * - a JSON reference holding only the fields declared in the reference meta (XxxIdReference.FIELDS).
 */
public final class References {

    private References() {}

    // Every XxxIdReference declares its id and code under these keys
    private static final TypedAttribute<UUID> REF_ID = attr("id", UUID.class);
    private static final TypedAttribute<String> REF_CODE = attr("code", String.class);

    /**
     * Copies only the given fields from source. Fields absent in source are skipped.
     */
    public static Map<String, Object> pick(Map<String, Object> source, List<TypedAttribute<?>> fields) {
        Map<String, Object> ref = new HashMap<>();
        if (source == null) return ref;

        DataRecord sourceRecord = DataRecord.ofNullable(source);
        for (TypedAttribute<?> field : fields) {
            if (sourceRecord.contains(field)) {
                ref.put(field.getFieldName(), sourceRecord.get(field));
            }
        }
        return ref;
    }

    /**
     * Copies only the given fields from source, for a source whose keys differ from the reference.
     * Each field is read from the source key given in sourceFields (reference field -> source field),
     * or from its own key when it has no entry there.
     * e.g. {@code Map.of(InvoiceAccountSiteIdReference.ID, CustomerIdReference.ACCOUNT_SITE_ID)}
     */
    public static Map<String, Object> pick(Map<String, Object> source,
                                           List<TypedAttribute<?>> fields,
                                           Map<TypedAttribute<?>, TypedAttribute<?>> sourceFields) {
        if (source == null) return new HashMap<>();

        Map<String, Object> renamed = new HashMap<>();
        for (TypedAttribute<?> field : fields) {
            TypedAttribute<?> sourceField = sourceFields.getOrDefault(field, field);
            if (source.containsKey(sourceField.getFieldName())) {
                renamed.put(field.getFieldName(), source.get(sourceField.getFieldName()));
            }
        }
        return pick(renamed, fields);
    }

    /**
     * Sets the id column and the JSON reference of idColumn on target.
     *
     * @return the stored reference
     */
    public static DataRecord setReference(DataRecord target,
                                          TypedAttribute<UUID> idColumn,
                                          Map<String, Object> source,
                                          List<TypedAttribute<?>> fields) {
        return setReference(target, idColumn, null, source, fields);
    }

    /**
     * Sets the id column, the code column (when given) and the JSON reference of idColumn on target.
     *
     * @return the stored reference
     */
    public static DataRecord setReference(DataRecord target,
                                          TypedAttribute<UUID> idColumn,
                                          TypedAttribute<String> codeColumn,
                                          Map<String, Object> source,
                                          List<TypedAttribute<?>> fields) {
        return store(target, idColumn, codeColumn, pick(source, fields));
    }

    /**
     * Same as {@link #setReference(DataRecord, TypedAttribute, TypedAttribute, Map, List)},
     * for a source whose keys differ from the reference (see {@link #pick(Map, List, Map)}).
     *
     * @return the stored reference
     */
    public static DataRecord setReference(DataRecord target,
                                          TypedAttribute<UUID> idColumn,
                                          TypedAttribute<String> codeColumn,
                                          Map<String, Object> source,
                                          List<TypedAttribute<?>> fields,
                                          Map<TypedAttribute<?>, TypedAttribute<?>> sourceFields) {
        return store(target, idColumn, codeColumn, pick(source, fields, sourceFields));
    }

    private static DataRecord store(DataRecord target,
                                    TypedAttribute<UUID> idColumn,
                                    TypedAttribute<String> codeColumn,
                                    Map<String, Object> ref) {
        DataRecord refRecord = DataRecord.of(ref);

        target.set(idColumn, refRecord.get(REF_ID));
        if (codeColumn != null) {
            target.set(codeColumn, refRecord.get(REF_CODE));
        }
        target.setRef(idColumn, refRecord.getValues());
        return refRecord;
    }
}
