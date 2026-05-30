package com.msm.core.dynamicquery.command;

import com.msm.core.commons.Utils;
import com.msm.core.dynamicquery.ConditionUtils;
import com.msm.core.exceptions.CommonErrors;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Query;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DefaultDynamicDelete implements DynamicDelete{
    private final DSLContext dsl;
    private final DynamicUpdate dynamicUpdate;

    public int deleteById(ObjectMetadata meta, Object id, Map<String, Object> item) {
        if (id == null) {
            throw CommonErrors.required("id", "Id must not be null");
        }

        return dynamicUpdate.updateById(meta, id, item);
    }

    public int delete(ObjectMetadata meta, Map<String, Object> item, Condition condition) {
        return dynamicUpdate.update(meta, item, condition);
    }

    public int delete(ObjectMetadata meta, List<Map<String, Object>> items) {
        return dynamicUpdate.update(meta, items);
    }

    @Override
    public int[] batchDelete(ObjectMetadata meta, List<Map<String, Object>> items) {
        return dynamicUpdate.batchUpdate(meta, items);
    }

    public Map<String, Object> deleteByIdReturning(ObjectMetadata meta, Object id, Map<String, Object> item) {
        if (id == null) {
            throw CommonErrors.required("id", "Id must not be null");
        }

        return dynamicUpdate.updateByIdReturning(meta, id, item);
    }


    public Map<String, Object> deleteReturning(ObjectMetadata meta, Map<String, Object> item, Condition condition) {
        return dynamicUpdate.updateReturning(meta, item, condition);
    }

    public List<Map<String, Object>> deleteReturning(ObjectMetadata meta, List<Map<String, Object>> items, List<String> returnFields) {
        return dynamicUpdate.updateReturning(meta, items, returnFields);
    }

    public int forceDeleteById(ObjectMetadata meta, Object id) {
        if (id == null) {
            throw CommonErrors.required("id", "Id must not be null");
        }

        Attribute attribute = meta.getIdAttribute();
        Field<Object> idField = (Field<Object>) attribute.getField();
        Object idCasted = attribute.cast(id);
        Condition condition = idField.eq(idCasted);
        return dsl.deleteFrom(meta.getTable())
                .where(condition)
                .execute();
    }

    public int forceDeleteByIds(ObjectMetadata meta, List<Object> ids) {

        Attribute attribute = meta.getIdAttribute();
        Field<Object> idField = (Field<Object>) attribute.getField();

        List<Object> idsCasted = Utils.CL.emptyIfNull(ids)
                .stream()
                .map(attribute::cast)
                .filter(Objects::nonNull)
                .toList();

        if (idsCasted.isEmpty()) {
            throw CommonErrors.required("ids", "Id must not be empty");
        }
        Condition condition = idField.in(idsCasted);
        return dsl.deleteFrom(meta.getTable())
                .where(condition)
                .execute();
    }

    @Override
    public int[] batchForceDeleteByIds(ObjectMetadata meta, List<Object> ids) {
        if (Utils.CL.isEmpty(ids)) {
            throw CommonErrors.required("ids", "Ids must not be empty");
        }

        Attribute attribute = meta.getIdAttribute();
        Field<Object> idField = (Field<Object>) attribute.getField();

        List<Query> queries = ids.stream()
                .map(attribute::cast)
                .map(id -> dsl.deleteFrom(meta.getTable()).where(idField.eq(id)))
                .collect(Collectors.toList());

        return dsl.batch(queries).execute();
    }

    public int forceDeleteById(ObjectMetadata meta, Condition where) {
        ConditionUtils.requireWhereCondition(where);
        return dsl.deleteFrom(meta.getTable())
                .where(where)
                .execute();
    }
}
