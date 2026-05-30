package com.msm.core.dynamicquery.command;

import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Condition;

import java.util.List;
import java.util.Map;

/**
 * Dynamic delete operations for metadata-driven entities.
 *
 * <p>Supports:
 * <ul>
 *     <li>Soft delete via update</li>
 *     <li>Hard delete (physical delete)</li>
 *     <li>Delete with RETURNING clause</li>
 *     <li>Batch delete operations</li>
 * </ul>
 */
public interface DynamicDelete {

    /**
     * Soft deletes a record by primary key.
     *
     * @param meta entity metadata
     * @param id primary key value
     * @param item delete payload
     * @return number of affected rows
     */
    int deleteById(
            ObjectMetadata meta,
            Object id,
            Map<String, Object> item);

    /**
     * Soft deletes records matching a condition.
     *
     * @param meta entity metadata
     * @param item delete payload
     * @param condition delete condition
     * @return number of affected rows
     */
    int delete(
            ObjectMetadata meta,
            Map<String, Object> item,
            Condition condition);

    /**
     * Batch soft delete.
     *
     * @param meta entity metadata
     * @param items delete payloads
     * @return total affected rows
     */
    int delete(
            ObjectMetadata meta,
            List<Map<String, Object>> items);

    /**
     * Batch soft delete.
     *
     * @param meta entity metadata
     * @param items delete payloads
     * @return affected rows for each statement
     */
    int[] batchDelete(
            ObjectMetadata meta,
            List<Map<String, Object>> items);

    /**
     * Soft deletes a record and returns deleted data.
     *
     * @param meta entity metadata
     * @param id primary key value
     * @param item delete payload
     * @return deleted row
     */
    Map<String, Object> deleteByIdReturning(
            ObjectMetadata meta,
            Object id,
            Map<String, Object> item);

    /**
     * Soft deletes records matching condition
     * and returns deleted data.
     *
     * @param meta entity metadata
     * @param item delete payload
     * @param condition delete condition
     * @return deleted row
     */
    Map<String, Object> deleteReturning(
            ObjectMetadata meta,
            Map<String, Object> item,
            Condition condition);

    /**
     * Batch soft delete and returns deleted rows.
     *
     * @param meta entity metadata
     * @param items delete payloads
     * @param returnFields fields to return
     * @return deleted rows
     */
    List<Map<String, Object>> deleteReturning(
            ObjectMetadata meta,
            List<Map<String, Object>> items,
            List<String> returnFields);

    /**
     * Physically deletes a record by primary key.
     *
     * @param meta entity metadata
     * @param id primary key value
     * @return number of affected rows
     */
    int forceDeleteById(
            ObjectMetadata meta,
            Object id);

    /**
     * Physically deletes multiple records by primary keys.
     *
     * @param meta entity metadata
     * @param ids primary key values
     * @return number of affected rows
     */
    int forceDeleteByIds(
            ObjectMetadata meta,
            List<Object> ids);

    /**
     * Batch physical delete by primary keys.
     *
     * @param meta entity metadata
     * @param ids primary key values
     * @return affected rows for each delete statement
     */
    int[] batchForceDeleteByIds(
            ObjectMetadata meta,
            List<Object> ids);

    /**
     * Physically deletes records matching a condition.
     *
     * @param meta entity metadata
     * @param where delete condition
     * @return number of affected rows
     */
    int forceDeleteById(
            ObjectMetadata meta,
            Condition where);
}
