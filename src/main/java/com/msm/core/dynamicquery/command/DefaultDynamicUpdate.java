package com.msm.core.dynamicquery.command;

import com.google.common.collect.Lists;
import com.msm.core.commons.Utils;
import com.msm.core.dynamicquery.ConditionUtils;
import com.msm.core.dynamicquery.SelectBuilder;
import com.msm.core.dynamicquery.locking.OptimisticLocks;
import com.msm.core.dynamicquery.mapping.DynamicQueryFieldValueMapper;
import com.msm.core.exceptions.CommonErrors;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class DefaultDynamicUpdate implements DynamicUpdate{
    private final DSLContext dsl;
    private final int BATCH_SIZE = 1000;

    @Override
    public List<Map<String, Object>> updateReturning(ObjectMetadata meta, List<Map<String, Object>> items) {
        return updateReturning(meta, items, meta.getFieldNames());
    }

    public List<Map<String, Object>> updateReturning(ObjectMetadata meta, List<Map<String, Object>> items, List<String> returnFields) {

        return Lists.partition(items, BATCH_SIZE)
                .stream()
                .flatMap(chunk -> bulkUpdateReturningChunk(meta, chunk, returnFields).stream())
                .toList();
    }

    private List<Map<String, Object>> bulkUpdateReturningChunk(
            ObjectMetadata meta,
            List<Map<String, Object>> items,
            List<String> returnFields
    ) {

        if (Utils.CL.isEmpty(items)) {
            return List.of();
        }

        Attribute idAttr = meta.getIdAttribute();
        Field<Object> idField = (Field<Object>) idAttr.getField();
        List<Query> queries = new ArrayList<>();
        List<Object> ids = new ArrayList<>();

        for (Map<String, Object> item : items) {

            Object objectId = item.get(idAttr.getFieldName());
            Object id = idAttr.cast(objectId);
            ids.add(id);

            Condition where = idField.eq(id);
            ConditionUtils.requireWhereCondition(where);
            Map<Field<?>, Object> map = DynamicQueryFieldValueMapper.toUpdateMap(meta, item);
            Condition versionCondition = OptimisticLocks.apply(meta, map);

            Query query = dsl.update(meta.getTable())
                    .set(map)
                    .where(where)
                    .and(versionCondition);

            queries.add(query);
        }

        int[] affected = dsl.batch(queries).execute();
        for (int i = 0; i < affected.length; i++) {
            if (affected[i] == 0) {
                throw CommonErrors.optimisticLockingFailureException(meta.getName(), "Version conflict at row: " + i);
            }
        }
        return dsl.select(SelectBuilder.buildFields(meta, returnFields))
                .from(meta.getTable())
                .where(idField.in(ids))
                .fetchMaps();
    }

    public int updateById(ObjectMetadata meta, Object id, Map<String, Object> values) {
        Attribute attribute = meta.getIdAttribute();
        Field<Object> fieldId = (Field<Object>) attribute.getField();
        Object idValueCasted = attribute.cast(id);
        return update(meta, values, fieldId.eq(idValueCasted));
    }

    public Map<String, Object> updateByIdReturning(ObjectMetadata meta, Object id, Map<String, Object> values) {
        return updateByIdReturning(meta, id, values, meta.getFieldNames());
    }

    public Map<String, Object> updateByIdReturning(ObjectMetadata meta, Object id, Map<String, Object> values, List<String> returnFields) {
        Attribute attribute = meta.getIdAttribute();
        Field<Object> fieldId = (Field<Object>) attribute.getField();
        Object idValueCasted = attribute.cast(id);

        return updateReturning(meta, values, fieldId.eq(idValueCasted), returnFields);
    }

    public Map<String, Object> updateReturning(ObjectMetadata meta, Map<String, Object> values, Condition where) {
        ConditionUtils.requireWhereCondition(where);
        Map<Field<?>, Object> map = DynamicQueryFieldValueMapper.toUpdateMap(meta, values);
        //Update new version and update where condition
        Condition versionCondition = OptimisticLocks.apply(meta, map);

        Map<String, Object> affected = dsl.update(meta.getTable())
                .set(map)
                .where(where)
                .and(versionCondition)
                .returning(SelectBuilder.buildFields(meta, meta.getFieldNames()))
                .fetchOneMap();

        if (Utils.CL.isEmpty(affected)) {
            throw CommonErrors.optimisticLockingFailureException(meta.getName(), "Version conflict or record not found");
        }

        return affected;
    }

    public Map<String, Object> updateReturning(ObjectMetadata meta, Map<String, Object> values, Condition where, List<String> returnFields) {
        ConditionUtils.requireWhereCondition(where);
        Map<Field<?>, Object> map = DynamicQueryFieldValueMapper.toUpdateMap(meta, values);
        //Update new version and update where condition
        Condition versionCondition = OptimisticLocks.apply(meta, map);

        Map<String, Object> affected = dsl.update(meta.getTable())
                .set(map)
                .where(where)
                .and(versionCondition)
                .returning(SelectBuilder.buildFields(meta, returnFields))
                .fetchOneMap();

        if (Utils.CL.isEmpty(affected)) {
            throw CommonErrors.optimisticLockingFailureException(meta.getName(), "Version conflict or record not found");
        }

        return affected;
    }

    public int update(ObjectMetadata meta, Map<String, Object> values, Condition where) {
        ConditionUtils.requireWhereCondition(where);
        Map<Field<?>, Object> map = DynamicQueryFieldValueMapper.toUpdateMap(meta, values);
        //Update new version and update where condition
        Condition versionCondition = OptimisticLocks.apply(meta, map);

        int affected = dsl.update(meta.getTable())
                .set(map)
                .where(where)
                .and(versionCondition)
                .execute();

        if (affected == 0) {
            throw CommonErrors.optimisticLockingFailureException(meta.getName(), "Version conflict or record not found");
        }

        return affected;
    }

    public int[] batchUpdate(ObjectMetadata meta, List<Map<String, Object>> items) {
        if (Utils.CL.isEmpty(items)) {
            return new int[]{};
        }

        Attribute idAttr = meta.getIdAttribute();
        Field<Object> idField = (Field<Object>) idAttr.getField();
        List<Query> queries = new ArrayList<>();

        for (Map<String, Object> item : items) {

            Object objectId = item.get(idAttr.getFieldName());
            Object id = idAttr.cast(objectId);
            Condition where = idField.eq(id);
            ConditionUtils.requireWhereCondition(where);
            Map<Field<?>, Object> map = DynamicQueryFieldValueMapper.toUpdateMap(meta, item);
            Condition versionCondition = OptimisticLocks.apply(meta, map);

            Query query = dsl.update(meta.getTable())
                    .set(map)
                    .where(where)
                    .and(versionCondition);

            queries.add(query);
        }

        int[] affected = dsl.batch(queries).execute();
        for (int i = 0; i < affected.length; i++) {
            if (affected[i] == 0) {
                throw CommonErrors.optimisticLockingFailureException(meta.getName(), "Version conflict at row: " + i);
            }
        }

        return affected;
    }

    @Override
    public int update(ObjectMetadata meta, List<Map<String, Object>> items) {
        int[] affected = batchUpdate(meta, items);
        return Arrays.stream(affected).sum();
    }
}
