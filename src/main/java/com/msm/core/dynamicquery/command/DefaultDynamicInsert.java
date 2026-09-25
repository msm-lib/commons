package com.msm.core.dynamicquery.command;

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
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DefaultDynamicInsert implements DynamicInsert{
    private final DSLContext dsl;

    public int insert(ObjectMetadata objectMetadata, Map<String, Object> values) {
        Map<Field<?>, Object> fieldValues = DynamicQueryFieldValueMapper.toInsertMap(objectMetadata, values);
        return dsl.insertInto(objectMetadata.getTable())
                .set(fieldValues)
                .execute();
    }

    private int[] batchInsert(ObjectMetadata objectMetadata, List<Map<String, Object>> values) {
        List<Map<Field<?>, Object>> fieldValues = values
                .stream()
                .map(objectMap -> DynamicQueryFieldValueMapper.toInsertMap(objectMetadata, objectMap))
                .collect(Collectors.toList());
//        List<TableRecord<?>> records = RecordBuilder.build(dsl, objectMetadata, fieldValues);
//        return dsl.batchInsert(records).execute();
        List<Field<?>> fields = new ArrayList<>(fieldValues.getFirst().keySet());

        var insertQuery = dsl.insertInto(objectMetadata.getTable()).columns(fields).values((Object[]) new Field<?>[fields.size()]);
        var batch = dsl.batch(insertQuery);

        for (Map<Field<?>, Object> row : fieldValues) {
            Object[] rowValues = fields.stream().map(row::get).toArray();
            batch.bind(rowValues);
        }
        return batch.execute();
    }

    public int[] insert(ObjectMetadata objectMetadata, List<Map<String, Object>> values) {
        return batchInsert(objectMetadata, values);
    }

    public Map<String, Object> insertReturning(ObjectMetadata objectMetadata, Map<String, Object> values) {
        return insertReturning(objectMetadata, values, objectMetadata.getFieldNames());
    }

    public Map<String, Object> insertReturning(ObjectMetadata meta, Map<String, Object> values, List<String> returnFields) {
        Map<Field<?>, Object> fieldValues = DynamicQueryFieldValueMapper.toInsertMap(meta, values);
        return dsl.insertInto(meta.getTable())
                .set(fieldValues)
                .returning(SelectBuilder.buildFields(meta, returnFields))
                .fetchOneMap();
    }

    public List<Map<String, Object>> insertReturning(ObjectMetadata objectMetadata, List<Map<String, Object>> items) {
        return insertReturning(objectMetadata, items, objectMetadata.getFieldNames());
    }

    public List<Map<String, Object>> insertReturning(ObjectMetadata meta, List<Map<String, Object>> items, List<String> returnFields) {
        if (Utils.CL.isEmpty(items)) {
            return Utils.CL.newArrayList();
        }
        List<Map<Field<?>, Object>> fieldValues = items
                .stream()
                .map(itemMap -> DynamicQueryFieldValueMapper.toInsertMap(meta, itemMap))
                .collect(Collectors.toList());

        List<Field<?>> fields = new ArrayList<>(fieldValues.getFirst().keySet());
        var insert = dsl.insertInto(meta.getTable()).columns(fields);

        for (Map<Field<?>, Object> row : fieldValues) {
            Object[] values = fields.stream().map(row::get).toArray();
            insert = insert.values(values);
        }

        return insert.returning(SelectBuilder.buildFields(meta, returnFields)).fetchMaps();
    }

    public List<Map<String, Object>> insertReturningInsertedRows(ObjectMetadata meta, List<Map<String, Object>> items, String conflictOnConstraintName) {
        if(Utils.STR.isBlank(conflictOnConstraintName)) {
            throw CommonErrors.required(conflictOnConstraintName, "conflictOnConstraintName must not be null or empty");
        }
        List<Map<Field<?>, Object>> fieldValues = items
                .stream()
                .map(objectMap -> DynamicQueryFieldValueMapper.toInsertMap(meta, objectMap))
                .collect(Collectors.toList());
        if (fieldValues.isEmpty()) {
            return Utils.CL.newArrayList();
        }
        List<Field<?>> fields = new ArrayList<>(fieldValues.getFirst().keySet());
        var insert = dsl.insertInto(meta.getTable()).columns(fields);

        for (Map<Field<?>, Object> itemFieldValue : fieldValues) {
            Object[] insertValues = fields.stream().map(itemFieldValue::get).toArray();
            insert = insert.values(insertValues);
        }

        return insert
                .onConflictOnConstraint(DSL.name(conflictOnConstraintName))
                .doNothing()
                .returning(meta.getFieldAlias())
                .fetchMaps();
    }


    public Map<String, Object> insertReturningInsertedRow(ObjectMetadata objectMetadata, Map<String, Object> values, String conflictOnConstraintName) {
        if(Utils.STR.isBlank(conflictOnConstraintName)) {
            throw CommonErrors.required(conflictOnConstraintName, "conflictOnConstraintName must not be null or empty");
        }
        Map<Field<?>, Object> fieldValues = DynamicQueryFieldValueMapper.toInsertMap(objectMetadata, values);
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

        Map<Field<?>, Object> fieldValues = DynamicQueryFieldValueMapper.toInsertMap(meta, values);
        Map<Field<?>, Object> updateSetMap = new LinkedHashMap<>();
        for (Field<?> field : fieldValues.keySet()) {
            updateSetMap.put(field, DSL.excluded(field));
        }

        return dsl.insertInto(meta.getTable())
                .set(fieldValues)
                .onConflictOnConstraint(DSL.name(conflictOnConstraintName))
                .doUpdate()
                .set(updateSetMap)
                .execute();
    }

    public int upsert(ObjectMetadata meta, Map<String, Object> values, List<String> conflictOnConstraintNames, Condition condition) {
        if (values == null || values.isEmpty()) {
            return 0;
        }

        if(Utils.CL.isEmpty(conflictOnConstraintNames)) {
            throw CommonErrors.required("conflictOnConstraintName", "conflictOnConstraintName must not be null or empty");
        }

        Map<Field<?>, Object> fieldValues = DynamicQueryFieldValueMapper.toInsertMap(meta, values);
        Map<Field<?>, Object> updateSetMap = new LinkedHashMap<>();
        for (Field<?> field : fieldValues.keySet()) {
            updateSetMap.put(field, DSL.excluded(field));
        }

        List<Field<Object>> conflictFields = conflictOnConstraintNames.stream().map(DSL::field).toList();
        Condition conflictFieldsCondition = Objects.nonNull(condition) ? condition : DSL.noCondition();


        return dsl.insertInto(meta.getTable())
                .set(fieldValues)
                .onConflict(conflictFields)
                .where(conflictFieldsCondition)
                .doUpdate()
                .set(updateSetMap)
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

        List<Map<Field<?>, Object>> fieldValuesList =
                values.stream()
                        .map(value ->
                                DynamicQueryFieldValueMapper
                                        .toInsertMap(meta, value)
                        )
                        .filter(Utils.CL::isNotEmpty)
                        .toList();

        if (fieldValuesList.isEmpty()) {
            return 0;
        }

        validateSameFields(fieldValuesList);

        List<Field<?>> fields = new ArrayList<>(fieldValuesList.getFirst().keySet());

        InsertValuesStepN<?> insert = dsl.insertInto(meta.getTable(), fields);

        for (Map<Field<?>, Object> fieldValues : fieldValuesList) {
            Object[] rowValues = fields.stream()
                    .map(fieldValues::get)
                    .toArray();

            insert = insert.values(rowValues);
        }

        Map<Field<?>, Object> updateSetMap =
                new LinkedHashMap<>();

        for (Field<?> field : fields) {
            updateSetMap.put(
                    field,
                    DSL.excluded(field)
            );
        }

        String conflictConstraint =
                conflictOnConstraintNames.getFirst();

        return insert
                .onConflictOnConstraint(
                        DSL.name(conflictConstraint)
                )
                .doUpdate()
                .set(updateSetMap)
                .execute();
    }

    private void validateSameFields(List<Map<Field<?>, Object>> fieldValuesList) {
        Set<Field<?>> expected = fieldValuesList.getFirst().keySet();

        for (int i = 1; i < fieldValuesList.size(); i++) {
            Set<Field<?>> actual = fieldValuesList.get(i).keySet();
            if (!expected.equals(actual)) {
                throw new IllegalArgumentException(
                        "Inconsistent fields in batch at index " + i +
                                ". Expected fields: " + expected +
                                ", actual fields: " + actual
                );
            }
        }
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


        List<Map<Field<?>, Object>> fieldValues = items.stream()
                .map(itemMap -> DynamicQueryFieldValueMapper.toInsertMap(meta, itemMap))
                .collect(Collectors.toList());

        List<Field<?>> fields = new ArrayList<>(fieldValues.getFirst().keySet());
        var insert = dsl.insertInto(meta.getTable()).columns(fields);

        for (Map<Field<?>, Object> row : fieldValues) {
            Object[] values = fields.stream().map(row::get).toArray();
            insert = insert.values(values);
        }

        Map<Field<?>, Object> updateSetMap = new LinkedHashMap<>();
        for (Field<?> field : fields) {
            updateSetMap.put(field, DSL.excluded(field));
        }

        return insert.onConflictOnConstraint(DSL.name(conflictOnConstraintName))
                .doUpdate()
                .set(updateSetMap)
                .returning(SelectBuilder.buildFields(meta, returnFields))
                .fetchMaps();
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

        List<Map<Field<?>, Object>> fieldValues = items.stream()
                .map(itemMap -> DynamicQueryFieldValueMapper.toInsertMap(meta, itemMap))
                .collect(Collectors.toList());

        List<Field<?>> fields = new ArrayList<>(fieldValues.getFirst().keySet());
        var insert = dsl.insertInto(meta.getTable()).columns(fields);

        for (Map<Field<?>, Object> row : fieldValues) {
            Object[] values = fields.stream().map(row::get).toArray();
            insert = insert.values(values);
        }

        Map<Field<?>, Object> updateSetMap = new LinkedHashMap<>();
        for (Field<?> field : fields) {
            updateSetMap.put(field, DSL.excluded(field));
        }

        List<Field<?>> targetFields = conflictFields.stream()
                .map(fieldName -> {
                    Attribute attribute = meta.getAttributeByName(fieldName);
                    if(attribute != null) {
                        return attribute.getField();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return insert.onConflict(targetFields)
                .doUpdate()
                .set(updateSetMap)
                .returning(SelectBuilder.buildFields(meta, returnFields))
                .fetchMaps();
    }

    @Override
    public int[] insertBatchIgnoreDuplicate(ObjectMetadata objectMetadata, List<Map<String, Object>> values) {
        if (values.isEmpty()) {
            return new int[0];
        }

        List<Map<Field<?>, Object>> fieldValues = values
                .stream()
                .map(objectMap ->
                        DynamicQueryFieldValueMapper.toInsertMap(objectMetadata, objectMap)
                ).collect(Collectors.toList());

        List<Field<?>> fields = new ArrayList<>(fieldValues.getFirst().keySet());

        var insertQuery = this.dsl
                .insertInto(objectMetadata.getTable())
                .columns(fields)
                .values((Object[]) new Field[fields.size()])
                .onConflictDoNothing();

        BatchBindStep batch = this.dsl.batch(insertQuery);

        for (Map<Field<?>, Object> row : fieldValues) {
            Object[] rowValues = fields.stream()
                    .map(row::get)
                    .toArray();
            batch.bind(rowValues);
        }

        return batch.execute();
    }

}
