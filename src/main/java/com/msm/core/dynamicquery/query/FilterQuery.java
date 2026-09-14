package com.msm.core.dynamicquery.query;

import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;
import com.msm.core.filter.domain.ObjectFilterRequest;
import com.msm.core.filter.domain.PageResponse;
import com.msm.core.filter.domain.pageable.Sort;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SortField;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
public interface FilterQuery {

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
     * Retrieves a list of dynamic records based on the specified filtering criteria, pagination limit, and sorting configuration.
     * <p>
     * This method constructs and executes a dynamic database query using the provided structural metadata.
     * It filters the dataset based on the given abstraction condition, projects only the requested return fields,
     * and limits the result set boundary accordingly.
     * </p>
     *
     * @param meta         the object metadata containing table mappings and structural attributes configuration
     * @param condition    the abstract query condition framework to be evaluated within the database filter clause
     * @param limit        the maximum number of records to retrieve from the database
     * @param sortFields   the list of sorting parameters specifying target fields and order directions
     * @param returnFields the list of field names to be projected in the select statement; if null or empty, all attributes defined in the metadata are selected
     * @return a {@link List} of {@link Map} objects, where each map represents a query record mapped by its field name and corresponding value
     * @throws RuntimeException or a generic persistence exception if any structural database error or execution failure occurs
     */
    List<Map<String, Object>> findByCondition(
            ObjectMetadata meta,
            Condition condition,
            int limit,
            List<Sort> sortFields,
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

    default Condition isDeleteCondition(ObjectMetadata meta) {
        Attribute isDeleteAttr = meta.getAttributeByName(Constants.IS_DELETED_FIELD);
        if (Objects.isNull(isDeleteAttr)) {
            return DSL.noCondition();
        }

        @SuppressWarnings("unchecked")
        Field<Boolean> isDeleteField = (Field<Boolean>) isDeleteAttr.getField();

        return DSL.or(
                isDeleteField.isNull(),
                isDeleteField.isFalse()
        );
    }

    default List<Map<String, Object>> internalFindByCondition(
            SelectConditionStep<Record> query,
            ObjectMetadata meta,
            int limit,
            List<Sort> sortFields) {
        List<SortField<?>> sortFieldList = SortingApplier.getSortField(meta, sortFields);
        if(Utils.CL.isNotEmpty(sortFieldList)) {
            query.orderBy(sortFieldList);
        }

        return query.limit(limit).fetchMaps();
    }
}
