package com.msm.core.dynamicquery.command;

import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Condition;

import java.util.List;
import java.util.Map;

/**
 * Dynamic update operations for metadata-driven entities.
 *
 * <p>Supports:
 * <ul>
 *     <li>Update by primary key</li>
 *     <li>Conditional update</li>
 *     <li>Batch update</li>
 *     <li>Update with RETURNING clause</li>
 *     <li>Optimistic locking support</li>
 * </ul>
 */
public interface DynamicUpdate {

    /**
     * Updates multiple records and returns all fields.
     *
     * @param meta entity metadata
     * @param items records to update
     * @return updated rows
     */
    List<Map<String, Object>> updateReturning(
            ObjectMetadata meta,
            List<Map<String, Object>> items);

    /**
     * Updates multiple records and returns selected fields.
     *
     * @param meta entity metadata
     * @param items records to update
     * @param returnFields fields to return
     * @return updated rows
     */
    List<Map<String, Object>> updateReturning(
            ObjectMetadata meta,
            List<Map<String, Object>> items,
            List<String> returnFields);

    /**
     * Updates a record by primary key.
     *
     * @param meta entity metadata
     * @param id primary key value
     * @param values updated values
     * @return number of affected rows
     */
    int updateById(
            ObjectMetadata meta,
            Object id,
            Map<String, Object> values);

    /**
     * Updates a record by primary key and returns all fields.
     *
     * @param meta entity metadata
     * @param id primary key value
     * @param values updated values
     * @return updated row
     */
    Map<String, Object> updateByIdReturning(
            ObjectMetadata meta,
            Object id,
            Map<String, Object> values);

    /**
     * Updates a record by primary key and returns selected fields.
     *
     * @param meta entity metadata
     * @param id primary key value
     * @param values updated values
     * @param returnFields fields to return
     * @return updated row
     */
    Map<String, Object> updateByIdReturning(
            ObjectMetadata meta,
            Object id,
            Map<String, Object> values,
            List<String> returnFields);

    /**
     * Updates the first record matching the condition
     * and returns all fields.
     *
     * @param meta entity metadata
     * @param values updated values
     * @param where update condition
     * @return updated row
     */
    Map<String, Object> updateReturning(
            ObjectMetadata meta,
            Map<String, Object> values,
            Condition where);

    /**
     * Updates the first record matching the condition
     * and returns selected fields.
     *
     * @param meta entity metadata
     * @param values updated values
     * @param where update condition
     * @param returnFields fields to return
     * @return updated row
     */
    Map<String, Object> updateReturning(
            ObjectMetadata meta,
            Map<String, Object> values,
            Condition where,
            List<String> returnFields);

    /**
     * Updates records matching the condition.
     *
     * @param meta entity metadata
     * @param values updated values
     * @param where update condition
     * @return number of affected rows
     */
    int update(
            ObjectMetadata meta,
            Map<String, Object> values,
            Condition where);

    /**
     * Batch update by primary key.
     *
     * @param meta entity metadata
     * @param items records to update
     * @return affected row count for each update statement
     */
    int[] batchUpdate(
            ObjectMetadata meta,
            List<Map<String, Object>> items);

    /**
     * Batch update and returns total affected rows.
     *
     * @param meta entity metadata
     * @param items records to update
     * @return total affected rows
     */
    int update(
            ObjectMetadata meta,
            List<Map<String, Object>> items);
}
