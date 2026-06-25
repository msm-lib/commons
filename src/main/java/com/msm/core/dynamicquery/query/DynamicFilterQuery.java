package com.msm.core.dynamicquery.query;

import com.msm.core.filter.domain.ObjectFilterRequest;
import com.msm.core.filter.domain.PageResponse;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Condition;

import java.util.List;
import java.util.Map;

/**
 * Dynamic query operations for metadata-driven entities.
 *
 * <p>Provides flexible read/query capabilities including:
 * <ul>
 *     <li>Pagination and filtering</li>
 *     <li>Lookup by primary key</li>
 *     <li>Lookup by multiple primary keys</li>
 *     <li>Conditional queries</li>
 *     <li>Single-record and multi-record retrieval</li>
 *     <li>Custom field projection</li>
 * </ul>
 */
public interface DynamicFilterQuery {

    /**
     * Executes a paginated query using the provided filter request.
     *
     * <p>The request may contain filtering conditions,
     * sorting criteria, pagination information, and
     * field selections.
     *
     * @param objectMetadata entity metadata
     * @param request filter request definition
     * @return paginated query result
     */
    PageResponse<Map<String, Object>> filter(
            ObjectMetadata objectMetadata,
            ObjectFilterRequest request);

    /**
     * @see #filter(ObjectMetadata, ObjectFilterRequest)
     */
    PageResponse<Map<String, Object>> lookup(
            ObjectMetadata objectMetadata,
            ObjectFilterRequest request);

    /**
     * Finds a record by primary key and returns selected fields.
     *
     * @param meta entity metadata
     * @param id primary key value
     * @param returnFields fields to return
     * @return matching record or {@code null} if not found
     */
    Map<String, Object> findById(
            ObjectMetadata meta,
            Object id,
            List<String> returnFields);

    /**
     * Finds a record by primary key and returns all fields.
     *
     * @param meta entity metadata
     * @param id primary key value
     * @return matching record or {@code null} if not found
     */
    Map<String, Object> findById(
            ObjectMetadata meta,
            Object id);

    /**
     * Finds multiple records by primary keys and returns selected fields.
     *
     * @param meta entity metadata
     * @param ids primary key values
     * @param returnFields fields to return
     * @return matching records
     */
    List<Map<String, Object>> findByIds(
            ObjectMetadata meta,
            List<Object> ids,
            List<String> returnFields);

    /**
     * Finds multiple records by primary keys and returns all fields.
     *
     * @param meta entity metadata
     * @param ids primary key values
     * @return matching records
     */
    List<Map<String, Object>> findByIds(
            ObjectMetadata meta,
            List<Object> ids);

    /**
     * Finds all records matching the specified condition.
     *
     * @param meta entity metadata
     * @param condition query condition
     * @return matching records
     */
    List<Map<String, Object>> findByCondition(
            ObjectMetadata meta,
            Condition condition);

    /**
     * Finds all records matching the specified condition
     * and returns selected fields.
     *
     * @param meta entity metadata
     * @param condition query condition
     * @param returnFields fields to return
     * @return matching records
     */
    List<Map<String, Object>> findByCondition(
            ObjectMetadata meta,
            Condition condition,
            List<String> returnFields);

    /**
     * Finds the first record matching the specified condition.
     *
     * @param meta entity metadata
     * @param condition query condition
     * @return matching record or {@code null} if not found
     */
    Map<String, Object> findOneByCondition(
            ObjectMetadata meta,
            Condition condition);

    /**
     * Finds the first record matching the specified condition
     * and returns selected fields.
     *
     * @param meta entity metadata
     * @param condition query condition
     * @param returnFields fields to return
     * @return matching record or {@code null} if not found
     */
    Map<String, Object> findOneByCondition(
            ObjectMetadata meta,
            Condition condition,
            List<String> returnFields);
}
