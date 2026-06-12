package com.msm.core.dynamicquery.command;

import com.msm.core.commons.Utils;
import com.msm.core.dynamicquery.SelectBuilder;
import com.msm.core.dynamicquery.mapping.DynamicQueryFieldValueMapper;
import com.msm.core.metadata.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        List<UpdatableRecord<?>> records = RecordBuilder.build(dsl, objectMetadata, fieldValues);
        return dsl.batchInsert(records).execute();
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

        if(Utils.STR.isBlank(conflictOnConstraintName)) {
            return insert
                    .onConflict()
                    .doNothing()
                    .returning(meta.getFieldAlias())
                    .fetchMaps();
        }

        return insert
                .onConflictOnConstraint(DSL.name(conflictOnConstraintName))
                .doNothing()
                .returning(meta.getFieldAlias())
                .fetchMaps();
    }


    public Map<String, Object> insertReturningInsertedRow(ObjectMetadata objectMetadata, Map<String, Object> values, String conflictOnConstraintName) {

        Map<Field<?>, Object> fieldValues = DynamicQueryFieldValueMapper.toInsertMap(objectMetadata, values);
        if(Utils.STR.isBlank(conflictOnConstraintName)) {
            return dsl.insertInto(objectMetadata.getTable())
                    .set(fieldValues)
                    .onConflict()
                    .doNothing()
                    .returning(objectMetadata.getFieldAlias())
                    .fetchOneMap();
        }
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

        Map<Field<?>, Object> fieldValues = DynamicQueryFieldValueMapper.toInsertMap(meta, values);
        Map<Field<?>, Object> updateSetMap = new LinkedHashMap<>();
        for (Field<?> field : fieldValues.keySet()) {
            updateSetMap.put(field, DSL.excluded(field));
        }
        if(Utils.STR.isBlank(conflictOnConstraintName)) {
            return dsl.insertInto(meta.getTable())
                    .set(fieldValues)
                    .onConflict()
                    .doUpdate()
                    .set(updateSetMap)
                    .execute();
        }
        return dsl.insertInto(meta.getTable())
                .set(fieldValues)
                .onConflictOnConstraint(DSL.name(conflictOnConstraintName))
                .doUpdate()
                .set(updateSetMap)
                .execute();
    }

    public List<Map<String, Object>> upsertReturning(ObjectMetadata meta, List<Map<String, Object>> items, String conflictOnConstraintName) {
        return upsertReturning(meta, items, conflictOnConstraintName, meta.getFieldNames());
    }

    public List<Map<String, Object>> upsertReturning(ObjectMetadata meta, List<Map<String, Object>> items, String conflictOnConstraintName, List<String> returnFields) {
        if (Utils.CL.isEmpty(items)) {
            return Utils.CL.newArrayList();
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

        if(Utils.STR.isBlank(conflictOnConstraintName)) {
            return insert
                    .onConflict()
                    .doUpdate()
                    .set(updateSetMap)
                    .returning(SelectBuilder.buildFields(meta, returnFields))
                    .fetchMaps();
        }

        return insert.onConflictOnConstraint(DSL.name(conflictOnConstraintName))
                .doUpdate()
                .set(updateSetMap)
                .returning(SelectBuilder.buildFields(meta, returnFields))
                .fetchMaps();
    }


}
