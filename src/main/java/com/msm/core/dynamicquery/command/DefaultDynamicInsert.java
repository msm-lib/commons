package com.msm.core.dynamicquery.command;

import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;
import com.msm.core.dynamicquery.SelectBuilder;
import com.msm.core.dynamicquery.mapping.DynamicQueryFieldValueMapper;
import com.msm.core.exceptions.CommonErrors;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.jooq.BatchBindStep;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.InsertValuesStepN;
import org.jooq.Query;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Multi-row methods group the rows by the set of columns they carry and run one statement per group. Inserts skip
 * absent keys and null values ({@link DynamicQueryFieldValueMapper#toInsertMap}); upserts skip absent keys only, a null
 * is written ({@link DynamicQueryFieldValueMapper#toUpsertMap}). Rows with the same columns, the usual case,
 * give a single statement. A column a row does not carry is left out of its statement: an insert gets the column
 * default, an upsert keeps the existing value. A column an upsert row carries as null is cleared.
 * <p>
 * Upserts never update the primary key nor the creation audit fields ({@code createdAt}, {@code createdBy},
 * {@code createdById}) of an existing row.
 */
@RequiredArgsConstructor
public class DefaultDynamicInsert implements DynamicInsert{
    private static final List<String> NON_UPDATABLE_FIELDS =
            List.of(Constants.OBJECT_PK, Constants.CREATED_AT, Constants.CREATED_BY, Constants.CREATED_BY_ID);

    private final DSLContext dsl;

    /** {@link DynamicQueryFieldValueMapper#toInsertMap} or {@link DynamicQueryFieldValueMapper#toUpsertMap}. */
    @FunctionalInterface
    private interface RowMapper {
        Map<Field<?>, Object> map(ObjectMetadata meta, Map<String, Object> values);
    }

    /** Rows with the same columns, in input order, with their index in the input list. */
    private record RowGroup(List<Field<?>> fields, List<Integer> indexes, List<Map<Field<?>, Object>> rows) {
        // a row's map iterates its columns in attribute order, the same order as fields
        List<Object[]> values() {
            return rows.stream().map(row -> row.values().toArray()).toList();
        }
    }

    public int insert(ObjectMetadata objectMetadata, Map<String, Object> values) {
        Map<Field<?>, Object> fieldValues = toRow(objectMetadata, values, 0, DynamicQueryFieldValueMapper::toInsertMap);
        return dsl.insertInto(objectMetadata.getTable())
                .set(fieldValues)
                .execute();
    }

    public int[] batchInsert(ObjectMetadata objectMetadata, List<Map<String, Object>> values) {
        return executeBatch(objectMetadata, values, insert -> insert);
    }

    public Map<String, Object> insertReturning(ObjectMetadata objectMetadata, Map<String, Object> values) {
        return insertReturning(objectMetadata, values, objectMetadata.getFieldNames());
    }

    public Map<String, Object> insertReturning(ObjectMetadata meta, Map<String, Object> values, List<String> returnFields) {
        Map<Field<?>, Object> fieldValues = toRow(meta, values, 0, DynamicQueryFieldValueMapper::toInsertMap);
        return dsl.insertInto(meta.getTable())
                .set(fieldValues)
                .returning(SelectBuilder.buildFields(meta, returnFields))
                .fetchOneMap();
    }

    public List<Map<String, Object>> insertReturning(ObjectMetadata objectMetadata, List<Map<String, Object>> items) {
        return insertReturning(objectMetadata, items, objectMetadata.getFieldNames());
    }

    public List<Map<String, Object>> insertReturning(ObjectMetadata meta, List<Map<String, Object>> items, List<String> returnFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (RowGroup group : groupByColumns(meta, items, DynamicQueryFieldValueMapper::toInsertMap)) {
            result.addAll(insertValues(meta, group)
                    .returning(SelectBuilder.buildFields(meta, returnFields))
                    .fetchMaps());
        }
        return result;
    }

    public List<Map<String, Object>> insertReturningInsertedRows(ObjectMetadata meta, List<Map<String, Object>> items, String conflictOnConstraintName) {
        if(Utils.STR.isBlank(conflictOnConstraintName)) {
            throw CommonErrors.required(conflictOnConstraintName, "conflictOnConstraintName must not be null or empty");
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (RowGroup group : groupByColumns(meta, items, DynamicQueryFieldValueMapper::toInsertMap)) {
            result.addAll(insertValues(meta, group)
                    .onConflictOnConstraint(DSL.name(conflictOnConstraintName))
                    .doNothing()
                    .returning(meta.getFieldAlias())
                    .fetchMaps());
        }
        return result;
    }


    public Map<String, Object> insertReturningInsertedRow(ObjectMetadata objectMetadata, Map<String, Object> values, String conflictOnConstraintName) {
        if(Utils.STR.isBlank(conflictOnConstraintName)) {
            throw CommonErrors.required(conflictOnConstraintName, "conflictOnConstraintName must not be null or empty");
        }
        Map<Field<?>, Object> fieldValues = toRow(objectMetadata, values, 0, DynamicQueryFieldValueMapper::toInsertMap);
        return dsl.insertInto(objectMetadata.getTable())
                .set(fieldValues)
                .onConflictOnConstraint(DSL.name(conflictOnConstraintName))
                .doNothing()
                .returning(objectMetadata.getFieldAlias())
                .fetchOneMap();
    }

    public int upsert(ObjectMetadata meta, Map<String, Object> values, String conflictOnConstraintName) {
        if (values == null || values.isEmpty()) {
            return 0;
        }

        if(Utils.STR.isBlank(conflictOnConstraintName)) {
            throw CommonErrors.required(conflictOnConstraintName, "conflictOnConstraintName must not be null or empty");
        }

        Map<Field<?>, Object> fieldValues = toRow(meta, values, 0, DynamicQueryFieldValueMapper::toUpsertMap);
        return dsl.insertInto(meta.getTable())
                .set(fieldValues)
                .onConflictOnConstraint(DSL.name(conflictOnConstraintName))
                .doUpdate()
                .set(updateSet(meta, fieldValues.keySet()))
                .execute();
    }

    public int upsert(ObjectMetadata meta, Map<String, Object> values, List<String> conflictFieldNames, Condition condition) {
        if (values == null || values.isEmpty()) {
            return 0;
        }

        if(Utils.CL.isEmpty(conflictFieldNames)) {
            throw CommonErrors.required("conflictFieldNames", "conflictFieldNames must not be null or empty");
        }

        Map<Field<?>, Object> fieldValues = toRow(meta, values, 0, DynamicQueryFieldValueMapper::toUpsertMap);
        Condition conflictFieldsCondition = Objects.nonNull(condition) ? condition : DSL.noCondition();

        return dsl.insertInto(meta.getTable())
                .set(fieldValues)
                .onConflict(conflictFields(meta, conflictFieldNames))
                .where(conflictFieldsCondition)
                .doUpdate()
                .set(updateSet(meta, fieldValues.keySet()))
                .execute();
    }

    public int upsertBatch(
            ObjectMetadata meta,
            List<Map<String, Object>> values,
            List<String> conflictOnConstraintNames
    ) {
        if (values == null || values.isEmpty()) {
            return 0;
        }

        if (conflictOnConstraintNames == null
                || conflictOnConstraintNames.isEmpty()) {
            throw CommonErrors.required("conflictOnConstraintNames", "conflictOnConstraintNames must not be null or empty");
        }

        String conflictConstraint =
                conflictOnConstraintNames.getFirst();

        int count = 0;
        for (RowGroup group : groupByColumns(meta, values, DynamicQueryFieldValueMapper::toUpsertMap)) {
            count += insertValues(meta, group)
                    .onConflictOnConstraint(
                            DSL.name(conflictConstraint)
                    )
                    .doUpdate()
                    .set(updateSet(meta, group.fields()))
                    .execute();
        }
        return count;
    }

    public List<Map<String, Object>> upsertReturning(ObjectMetadata meta, List<Map<String, Object>> items, String conflictOnConstraintName) {
        return upsertReturning(meta, items, conflictOnConstraintName, meta.getFieldNames());
    }

    public List<Map<String, Object>> upsertReturning(ObjectMetadata meta, List<Map<String, Object>> items, String conflictOnConstraintName, List<String> returnFields) {
        if (Utils.CL.isEmpty(items)) {
            return Utils.CL.newArrayList();
        }

        if(Utils.STR.isBlank(conflictOnConstraintName)) {
            throw CommonErrors.required(conflictOnConstraintName, "conflictOnConstraintName must not be null or empty");
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (RowGroup group : groupByColumns(meta, items, DynamicQueryFieldValueMapper::toUpsertMap)) {
            result.addAll(insertValues(meta, group)
                    .onConflictOnConstraint(DSL.name(conflictOnConstraintName))
                    .doUpdate()
                    .set(updateSet(meta, group.fields()))
                    .returning(SelectBuilder.buildFields(meta, returnFields))
                    .fetchMaps());
        }
        return result;
    }

    public List<Map<String, Object>> upsertReturning(
            ObjectMetadata meta,
            List<Map<String, Object>> items,
            List<String> conflictFields) {

        return upsertReturning(meta, items, conflictFields, meta.getFieldNames());
    }

    public List<Map<String, Object>> upsertReturning(
            ObjectMetadata meta,
            List<Map<String, Object>> items,
            List<String> conflictFields,
            List<String> returnFields) {

        if (Utils.CL.isEmpty(items)) {
            return Utils.CL.newArrayList();
        }

        if(Utils.CL.isEmpty(conflictFields)) {
            throw CommonErrors.required("conflictFields", "conflictFields must not be null or empty");
        }

        List<Field<?>> targetFields = conflictFields(meta, conflictFields);

        List<Map<String, Object>> result = new ArrayList<>();
        for (RowGroup group : groupByColumns(meta, items, DynamicQueryFieldValueMapper::toUpsertMap)) {
            result.addAll(insertValues(meta, group)
                    .onConflict(targetFields)
                    .doUpdate()
                    .set(updateSet(meta, group.fields()))
                    .returning(SelectBuilder.buildFields(meta, returnFields))
                    .fetchMaps());
        }
        return result;
    }

    @Override
    public int[] insertBatchIgnoreDuplicate(ObjectMetadata objectMetadata, List<Map<String, Object>> values) {
        return executeBatch(objectMetadata, values, insert -> insert.onConflictDoNothing());
    }

    /** JDBC batch insert, one batch per column set. Counts are returned in input order. */
    private int[] executeBatch(ObjectMetadata meta, List<Map<String, Object>> values, Function<InsertValuesStepN<?>, Query> finisher) {
        int[] counts = new int[Utils.CL.size(values)];
        for (RowGroup group : groupByColumns(meta, values, DynamicQueryFieldValueMapper::toInsertMap)) {
            var insertQuery = dsl.insertInto(meta.getTable())
                    .columns(group.fields())
                    .values((Object[]) new Field<?>[group.fields().size()]);
            BatchBindStep batch = dsl.batch(finisher.apply(insertQuery));
            group.values().forEach(batch::bind);

            int[] groupCounts = batch.execute();
            for (int i = 0; i < groupCounts.length; i++) {
                counts[group.indexes().get(i)] = groupCounts[i];
            }
        }
        return counts;
    }

    private InsertValuesStepN<?> insertValues(ObjectMetadata meta, RowGroup group) {
        InsertValuesStepN<?> insert = dsl.insertInto(meta.getTable(), group.fields());
        for (Object[] rowValues : group.values()) {
            insert = insert.values(rowValues);
        }
        return insert;
    }

    /** Maps one input row to columns; see {@link WriteRows#requireColumns}. */
    private static Map<Field<?>, Object> toRow(ObjectMetadata meta, Map<String, Object> values, int rowIndex, RowMapper rowMapper) {
        return WriteRows.requireColumns(meta, values, rowMapper.map(meta, Utils.CL.emptyIfNull(values)), rowIndex);
    }

    private static List<RowGroup> groupByColumns(ObjectMetadata meta, List<Map<String, Object>> items, RowMapper rowMapper) {
        Map<Set<String>, RowGroup> groups = new LinkedHashMap<>();
        for (int i = 0; i < Utils.CL.size(items); i++) {
            Map<Field<?>, Object> row = toRow(meta, items.get(i), i, rowMapper);
            Set<String> columns = row.keySet().stream().map(Field::getName).collect(Collectors.toSet());
            RowGroup group = groups.computeIfAbsent(columns,
                    key -> new RowGroup(new ArrayList<>(row.keySet()), new ArrayList<>(), new ArrayList<>()));
            group.indexes().add(i);
            group.rows().add(row);
        }
        return new ArrayList<>(groups.values());
    }

    /**
     * {@code col = excluded.col} for the inserted columns, except the primary key and the creation audit fields.
     * When nothing is left, a no-op assignment keeps {@code DO UPDATE} valid, so RETURNING still returns the row.
     */
    private static Map<Field<?>, Object> updateSet(ObjectMetadata meta, Collection<Field<?>> fields) {
        Set<String> nonUpdatableColumns = NON_UPDATABLE_FIELDS.stream()
                .map(meta::getAttributeByName)
                .filter(Objects::nonNull)
                .map(Attribute::getColumnName)
                .collect(Collectors.toSet());

        Map<Field<?>, Object> updateSetMap = new LinkedHashMap<>();
        for (Field<?> field : fields) {
            if (!nonUpdatableColumns.contains(field.getName())) {
                updateSetMap.put(field, DSL.excluded(field));
            }
        }
        if (updateSetMap.isEmpty()) {
            Field<?> field = fields.iterator().next();
            updateSetMap.put(field, DSL.field(DSL.name(meta.getTableName(), field.getName())));
        }
        return updateSetMap;
    }

    /** Conflict target columns, given as field names (or column names). */
    private static List<Field<?>> conflictFields(ObjectMetadata meta, List<String> names) {
        return names.stream()
                .map(name -> Stream.ofNullable(meta.getAttributeByName(name))
                        .findFirst()
                        .or(() -> Utils.CL.emptyIfNull(meta.getAttributes()).stream()
                                .filter(attribute -> name.equals(attribute.getColumnName()))
                                .findFirst())
                        .orElseThrow(() -> CommonErrors.fieldNotFoundException(name,
                                "Conflict field '" + name + "' not found in object " + meta.getName())))
                .<Field<?>>map(Attribute::getField)
                .toList();
    }
}
