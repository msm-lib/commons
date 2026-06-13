package com.msm.core.dynamicquery;

import com.msm.core.dynamicquery.command.DynamicDelete;
import com.msm.core.dynamicquery.command.DynamicInsert;
import com.msm.core.dynamicquery.command.DynamicUpdate;
import com.msm.core.dynamicquery.query.DynamicFilterQuery;
import com.msm.core.filter.domain.ObjectFilterRequest;
import com.msm.core.filter.domain.PageResponse;
import com.msm.core.metadata.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;

import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link DynamicObjectQuery}.
 *
 * <p>This service acts as a facade over the dynamic persistence layer by
 * aggregating query, insert, update, and delete operations into a single
 * entry point.
 *
 * <p>All operations are delegated to their corresponding specialized
 * components:
 * <ul>
 *     <li>{@link DynamicFilterQuery} for query and filtering operations</li>
 *     <li>{@link DynamicInsert} for insert operations</li>
 *     <li>{@link DynamicUpdate} for update operations</li>
 *     <li>{@link DynamicDelete} for delete operations</li>
 * </ul>
 *
 * <p>This class does not contain business logic. Its primary responsibility
 * is to expose a unified API for metadata-driven CRUD operations while
 * delegating execution to the appropriate implementation.
 *
 * <p>Consumers should prefer using {@link DynamicObjectQuery} or
 * {@code DynamicQueryService} instead of interacting directly with the
 * underlying operation-specific services.
 *
 */
@RequiredArgsConstructor
public class DynamicQueryService implements DynamicObjectQuery {

    private final DynamicFilterQuery query;
    private final DynamicInsert insert;
    private final DynamicUpdate update;
    private final DynamicDelete delete;


    @Override
    public PageResponse<Map<String, Object>> filter(ObjectMetadata objectMetadata, ObjectFilterRequest request) {
        return query.filter(objectMetadata, request);
    }

    @Override
    public Map<String, Object> findById(ObjectMetadata meta, Object id, List<String> returnFields) {
        return query.findById(meta, id, returnFields);
    }

    @Override
    public Map<String, Object> findById(ObjectMetadata meta, Object id) {
        return query.findById(meta, id);
    }

    @Override
    public List<Map<String, Object>> findByIds(ObjectMetadata meta, List<Object> ids, List<String> returnFields) {
        return query.findByIds(meta, ids, returnFields);
    }

    @Override
    public List<Map<String, Object>> findByIds(ObjectMetadata meta, List<Object> ids) {
        return query.findByIds(meta, ids);
    }

    @Override
    public List<Map<String, Object>> findByCondition(ObjectMetadata meta, Condition condition) {
        return query.findByCondition(meta, condition);
    }

    @Override
    public List<Map<String, Object>> findByCondition(ObjectMetadata meta, Condition condition, List<String> returnFields) {
        return query.findByCondition(meta, condition, returnFields);
    }

    @Override
    public Map<String, Object> findOneByCondition(ObjectMetadata meta, Condition condition) {
        return query.findOneByCondition(meta, condition);
    }

    @Override
    public Map<String, Object> findOneByCondition(ObjectMetadata meta, Condition condition, List<String> returnFields) {
        return query.findOneByCondition(meta, condition, returnFields);
    }

    @Override
    public int deleteById(ObjectMetadata meta, Object id, Map<String, Object> item) {
        return delete.deleteById(meta, id, item);
    }

    @Override
    public int delete(ObjectMetadata meta, Map<String, Object> item, Condition condition) {
        return delete.delete(meta, item, condition);
    }

    @Override
    public int delete(ObjectMetadata meta, List<Map<String, Object>> items) {
        return delete.delete(meta, items);
    }

    @Override
    public int[] batchDelete(ObjectMetadata meta, List<Map<String, Object>> items) {
        return delete.batchDelete(meta, items);
    }

    @Override
    public Map<String, Object> deleteByIdReturning(ObjectMetadata meta, Object id, Map<String, Object> item) {
        return delete.deleteByIdReturning(meta, id, item);
    }

    @Override
    public Map<String, Object> deleteReturning(ObjectMetadata meta, Map<String, Object> item, Condition condition) {
        return delete.deleteReturning(meta, item, condition);
    }

    @Override
    public List<Map<String, Object>> deleteReturning(ObjectMetadata meta, List<Map<String, Object>> items, List<String> returnFields) {
        return delete.deleteReturning(meta, items, returnFields);
    }

    @Override
    public int forceDeleteById(ObjectMetadata meta, Object id) {
        return delete.forceDeleteById(meta, id);
    }

    @Override
    public int forceDeleteByIds(ObjectMetadata meta, List<Object> ids) {
        return delete.forceDeleteByIds(meta, ids);
    }

    @Override
    public int[] batchForceDeleteByIds(ObjectMetadata meta, List<Object> ids) {
        return delete.batchForceDeleteByIds(meta, ids);
    }

    @Override
    public int forceDeleteById(ObjectMetadata meta, Condition where) {
        return delete.forceDeleteById(meta, where);
    }

    @Override
    public int insert(ObjectMetadata meta, Map<String, Object> values) {
        return insert.insert(meta, values);
    }

    @Override
    public int[] insert(ObjectMetadata meta, List<Map<String, Object>> values) {
        return insert.insert(meta, values);
    }

    @Override
    public Map<String, Object> insertReturning(ObjectMetadata meta, Map<String, Object> values) {
        return insert.insertReturning(meta, values);
    }

    @Override
    public Map<String, Object> insertReturning(ObjectMetadata meta, Map<String, Object> values, List<String> returnFields) {
        return insert.insertReturning(meta, values, returnFields);
    }

    @Override
    public List<Map<String, Object>> insertReturning(ObjectMetadata meta, List<Map<String, Object>> items) {
        return insert.insertReturning(meta, items);
    }

    @Override
    public List<Map<String, Object>> insertReturning(ObjectMetadata meta, List<Map<String, Object>> items, List<String> returnFields) {
        return insert.insertReturning(meta, items, returnFields);
    }

    @Override
    public List<Map<String, Object>> insertReturningInsertedRows(ObjectMetadata meta, List<Map<String, Object>> items, String conflictOnConstraintName) {
        return insert.insertReturningInsertedRows(meta, items, conflictOnConstraintName);
    }

    @Override
    public Map<String, Object> insertReturningInsertedRow(ObjectMetadata objectMetadata, Map<String, Object> values, String conflictOnConstraintName) {
        return insert.insertReturningInsertedRow(objectMetadata, values, conflictOnConstraintName);
    }

    @Override
    public int upsert(ObjectMetadata meta, Map<String, Object> values, String conflictOnConstraintName) {
        return insert.upsert(meta, values, conflictOnConstraintName);
    }

    @Override
    public List<Map<String, Object>> upsertReturning(ObjectMetadata meta, List<Map<String, Object>> items, String conflictOnConstraintName) {
        return insert.upsertReturning(meta, items, conflictOnConstraintName);
    }

    @Override
    public List<Map<String, Object>> upsertReturning(ObjectMetadata meta, List<Map<String, Object>> items, String conflictOnConstraintName, List<String> returnFields) {
        return insert.upsertReturning(meta, items, conflictOnConstraintName, returnFields);
    }

    @Override
    public List<Map<String, Object>> upsertReturning(ObjectMetadata meta, List<Map<String, Object>> items, List<String> conflictFields) {
        return insert.upsertReturning(meta, items, conflictFields);
    }

    @Override
    public List<Map<String, Object>> upsertReturning(ObjectMetadata meta, List<Map<String, Object>> items, List<String> conflictFields, List<String> returnFields) {
        return insert.upsertReturning(meta, items, conflictFields, returnFields);
    }

    @Override
    public List<Map<String, Object>> updateReturning(ObjectMetadata meta, List<Map<String, Object>> items) {
        return update.updateReturning(meta, items);
    }

    @Override
    public List<Map<String, Object>> updateReturning(ObjectMetadata meta, List<Map<String, Object>> items, List<String> returnFields) {
        return update.updateReturning(meta, items, returnFields);
    }

    @Override
    public int updateById(ObjectMetadata meta, Object id, Map<String, Object> values) {
        return update.updateById(meta, id, values);
    }

    @Override
    public Map<String, Object> updateByIdReturning(ObjectMetadata meta, Object id, Map<String, Object> values) {
        return update.updateByIdReturning(meta, id, values);
    }

    @Override
    public Map<String, Object> updateByIdReturning(ObjectMetadata meta, Object id, Map<String, Object> values, List<String> returnFields) {
        return update.updateByIdReturning(meta, id, values, returnFields);
    }

    @Override
    public Map<String, Object> updateReturning(ObjectMetadata meta, Map<String, Object> values, Condition where) {
        return update.updateReturning(meta, values, where);
    }

    @Override
    public Map<String, Object> updateReturning(ObjectMetadata meta, Map<String, Object> values, Condition where, List<String> returnFields) {
        return update.updateReturning(meta, values, where, returnFields);
    }

    @Override
    public int update(ObjectMetadata meta, Map<String, Object> values, Condition where) {
        return update.update(meta, values, where);
    }

    @Override
    public int[] batchUpdate(ObjectMetadata meta, List<Map<String, Object>> items) {
        return update.batchUpdate(meta, items);
    }

    @Override
    public int update(ObjectMetadata meta, List<Map<String, Object>> items) {
        return update.update(meta, items);
    }
}