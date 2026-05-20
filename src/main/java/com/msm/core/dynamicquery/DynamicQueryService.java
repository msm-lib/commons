package com.msm.core.dynamicquery;

import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;
import com.msm.core.exceptions.CommonErrors;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.filter.domain.FilterGroup;
import com.msm.core.filter.domain.FilterOperator;
import com.msm.core.filter.domain.LogicalOperator;
import com.msm.core.filter.domain.ObjectFilterRequest;
import com.msm.core.filter.domain.PageResponse;
import com.msm.core.filter.domain.pageable.Sort;
import com.msm.core.filter.domain.pageable.SortDirection;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <h2>DynamicQueryService</h2>
 *
 * <h3>Overview</h3>
 * <p>
 * A generic, metadata-driven data access service that enables dynamic interaction
 * with the database without relying on static entity models.
 * </p>
 * <p>
 * This service acts as the core execution layer for handling flexible data
 * structures using runtime metadata definitions.
 * </p>
 *
 * <hr>
 *
 * <h3>Core Responsibilities</h3>
 * <ul>
 *   <li>Dynamic filtering based on metadata and request models</li>
 *   <li>Sorting and pagination support</li>
 *   <li>Generic CRUD operations (insert, update, delete)</li>
 *   <li>Batch processing capabilities</li>
 *   <li>Optimistic locking support</li>
 * </ul>
 *
 * <hr>
 *
 * <h3>Design Principles</h3>
 * <ul>
 *   <li>Metadata-driven schema definition</li>
 *   <li>Map-based data processing for maximum flexibility</li>
 *   <li>Safe query execution with enforced WHERE conditions</li>
 *   <li>Separation of concerns for extensibility</li>
 * </ul>
 *
 * <hr>
 *
 * <h3>Extension Points</h3>
 * <ul>
 *   <li>Audit interceptor layer</li>
 *   <li>Row-level security (ABAC)</li>
 *   <li>Field-level validation rules</li>
 *   <li>Custom expression evaluation engine</li>
 * </ul>
 *
 * <hr>
 *
 * <h3>Note</h3>
 * <p>
 * This service is designed to be the foundation of a dynamic data platform,
 * where schema, behavior, and policies can be configured at runtime.
 * </p>
 */
@SuppressWarnings({"unchecked"})
@RequiredArgsConstructor
public class DynamicQueryService {

    private final DSLContext dsl;

    /**
     * Executes dynamic filtering with optional pagination.
     *
     * <p>Supports:</p>
     * <ul>
     *   <li>Dynamic filter conditions</li>
     *   <li>Soft delete filtering (if configured)</li>
     *   <li>Sorting rules</li>
     *   <li>Pagination (limit / offset)</li>
     * </ul>
     *
     * @param objectMetadata metadata definition of target object
     * @param request filter request containing conditions and paging
     * @return paginated or full dataset result
     */
    public PageResponse<Map<String, Object>> filter(ObjectMetadata objectMetadata, ObjectFilterRequest request) {
        SearchOrDefaultFilter.resolveSearchFilter(request);
        SearchOrDefaultFilter.addIsDeletedFilter(objectMetadata, request);
        Table<?> table = objectMetadata.getTable();

        Condition condition = FilterBuilder.build(request.getFilters(), objectMetadata);
        SelectConditionStep<Record> query = dsl
                .select(SelectBuilder.buildFields(objectMetadata, request))
                .from(table)
                .where(condition);

        applySorting(query, request, objectMetadata);
        applyPaging(query, request);

        List<Map<String, Object>> result = query.fetchMaps();
        if(Objects.nonNull(request.getPageRequest())) {
            Long total = dsl
                    .selectCount()
                    .from(table)
                    .where(condition)
                    .fetchOne(0, Long.class);

            if (total == null) {
                total = 0L;
            }
            return PageResponse.of(result, total, request.getPageRequest().getPage(), request.getPageRequest().getSize());
        }

        return PageResponse.of(result);
    }

    /**
     * Applies sorting rules from request.
     *
     * <p>Sorting is resolved dynamically based on metadata field mapping.
     *
     * @param query query object
     * @param request sorting configuration
     * @param objectMetadata metadata definition
     */
    private void applySorting(SelectConditionStep<org.jooq.Record> query,
                              ObjectFilterRequest request,
                              ObjectMetadata objectMetadata) {

        if (Objects.isNull(request.getPageRequest())) return;
        List<Sort> sorts = request.getPageRequest().getSorts();
        if (Objects.isNull(sorts)) return;

        List<SortField<?>> sortFields = sorts.stream()
                .map(s -> {
                    Field<?> field = FieldResolver.resolve(objectMetadata, s.getAttribute());
                    return SortDirection.ASC.name().equalsIgnoreCase(s.getDirection().name())
                            ? field.asc()
                            : field.desc();
                })
                .collect(Collectors.toList());

        query.orderBy(sortFields);
    }

    /**
     * Applies pagination rules (limit / offset).
     *
     * @param query query object
     * @param request paging configuration
     */
    private void applyPaging(SelectConditionStep<org.jooq.Record> query,
                             ObjectFilterRequest request) {

        if (Objects.isNull(request.getPageRequest())) return;
        int size = request.getPageRequest().getSize();
        int offset = request.getPageRequest().getOffset();

        query.limit(size).offset(offset);
    }

    /**
     * Converts input data into internal field-value mapping.
     *
     * <p>Processing steps:
     * <ul>
     *   <li>Resolve field definition from metadata</li>
     *   <li>Apply type casting rules</li>
     *   <li>Ignore unmapped or null values</li>
     * </ul>
     *
     * @param objectMetadata metadata definition
     * @param values raw input data
     * @return mapped field-value structure
     */
    private Map<Field<?>, Object> getFieldValues(ObjectMetadata objectMetadata, Map<String, Object> values) {
        List<Attribute> attrs = objectMetadata.getAttributes();
        Map<Field<?>, Object> fieldValues = new LinkedHashMap<>();
        for (Attribute attr : attrs) {
            if (!values.containsKey(attr.getFieldName())) continue;
            Object rawValue = values.get(attr.getFieldName());
            Object casted = attr.cast(rawValue);
            if (casted != null) {
                Field<?> field = attr.getField();
                fieldValues.put(field, casted);
            }
        }
        return fieldValues;
    }

    /**
     * Inserts a single record.
     *
     * @param objectMetadata metadata definition
     * @param values input data
     * @return number of affected rows
     */
    public int insert(ObjectMetadata objectMetadata, Map<String, Object> values) {
        Map<Field<?>, Object> fieldValues = getFieldValues(objectMetadata, values);
        return dsl.insertInto(objectMetadata.getTable())
                .set(fieldValues)
                .execute();
    }

    /**
     * Inserts a record and returns selected fields.
     *
     * @param objectMetadata metadata definition
     * @param values input data
     * @param returnFields fields to return
     * @return inserted record result
     */
    public Map<String, Object> insertReturning(ObjectMetadata objectMetadata, Map<String, Object> values, List<Field<?>> returnFields) {
        Map<Field<?>, Object> fieldValues = getFieldValues(objectMetadata, values);
        return dsl.insertInto(objectMetadata.getTable())
                .set(fieldValues)
                .returning(returnFields)
                .fetchOneMap();
    }

    /**
     * Inserts a record and returns all fields.
     *
     * @param objectMetadata metadata definition
     * @param values input data
     * @return inserted record result
     */
    public Map<String, Object> insertReturning(ObjectMetadata objectMetadata, Map<String, Object> values) {
        return insertReturning(objectMetadata, values, objectMetadata.getFieldAlias());
    }

    public Map<String, Object> insertReturningInsertedRow(ObjectMetadata objectMetadata, Map<String, Object> values, String conflictOnConstraintName) {
        Map<Field<?>, Object> fieldValues = getFieldValues(objectMetadata, values);
        return dsl.insertInto(objectMetadata.getTable())
                .set(fieldValues)
                .onConflictOnConstraint(DSL.name(conflictOnConstraintName))
                .doNothing()
                .returning(objectMetadata.getFieldAlias())
                .fetchOneMap();
    }


    /**
     * Builds record structures for batch processing.
     *
     * @param meta object metadata
     * @param rows list of field-value mappings
     * @return list of prepared records
     */
    private List<UpdatableRecord<?>> buildRecords(ObjectMetadata meta, List<Map<Field<?>, Object>> rows) {
        List<UpdatableRecord<?>> records = new ArrayList<>();
        for (Map<Field<?>, Object> row : rows) {
            UpdatableRecord<?> record = (UpdatableRecord<?>) dsl.newRecord(meta.getTable());
            row.forEach((key, value) -> {
                record.set((Field<Object>)key, value);
            });
            records.add(record);
        }

        return records;
    }

    /**
     * Executes batch insert operation for multiple records.
     *
     * <p>This method converts input maps into internal field-value mappings,
     * builds record structures, and performs batch insert.</p>
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Each input map is mapped to metadata-defined fields</li>
     *   <li>Values are cast to appropriate types</li>
     *   <li>Missing fields are ignored (NULL or DB default applied)</li>
     *   <li>Each record is inserted independently</li>
     * </ul>
     *
     * <p><b>Notes:</b></p>
     * <ul>
     *   <li>Does not return generated IDs</li>
     *   <li>Database default values may be applied</li>
     *   <li>Optimized for large datasets</li>
     * </ul>
     *
     * @param objectMetadata metadata definition
     * @param values list of records
     * @return update counts per record
     */
    public int[] batchInsert(ObjectMetadata objectMetadata, List<Map<String, Object>> values) {
        List<Map<Field<?>, Object>> fieldValues = values.stream().map(objectMap -> getFieldValues(objectMetadata, objectMap)).collect(Collectors.toList());
        List<UpdatableRecord<?>> records = buildRecords(objectMetadata, fieldValues);
        return dsl.batchInsert(records).execute();
    }

    /**
     * Inserts multiple records.
     *
     * <p>This is a convenience method that delegates to
     * {@link #batchInsert(ObjectMetadata, List)}.</p>
     *
     * <p>Use this method when inserting multiple records without requiring
     * returned values (e.g., generated IDs).</p>
     *
     * @param objectMetadata metadata definition of the target object
     * @param values list of records to insert
     * @return array of update counts corresponding to each insert operation
     */
    public int[] insert(ObjectMetadata objectMetadata, List<Map<String, Object>> values) {
        return batchInsert(objectMetadata, values);
    }

    /**
     * Inserts multiple records into the target table using a single
     * multi-values INSERT statement and returns the inserted records.
     *
     * <p>
     * This method supports dynamic schemas where insert fields are determined
     * at runtime from the provided row data.
     * </p>
     *
     * <p>
     * All rows must contain the same set of fields (same column structure).
     * The field order from the first row is used as the insert column order
     * for all subsequent rows.
     * </p>
     *
     * <p>
     * Example generated SQL:
     * </p>
     *
     * <pre>{@code
     * INSERT INTO users(name, email)
     * VALUES
     *   (?, ?),
     *   (?, ?)
     * RETURNING *;
     * }</pre>
     *
     * <p>
     * This approach supports RETURNING clauses and allows fetching generated IDs
     * and inserted records.
     * </p>
     *
     * @param objectMetadata
     *         metadata containing the target table definition
     *
     * @param rows
     *         list of rows to insert where:
     *         <ul>
     *             <li>key = field definition</li>
     *             <li>value = field value</li>
     *         </ul>
     *
     * @return inserted records as a list of {@code Map<String, Object>}
     *
     * @throws IllegalArgumentException
     *         if rows contain different field sets
     */
    public List<Map<String, Object>> insertReturning(ObjectMetadata objectMetadata, List<Map<Field<?>, Object>> rows, List<Field<?>> returnFields) {
        if (Utils.CL.isEmpty(rows)) {
            return Utils.CL.newArrayList();
        }
        List<Field<?>> fields = new ArrayList<>(rows.getFirst().keySet());
        var insert = dsl.insertInto(objectMetadata.getTable()).columns(fields);

        for (Map<Field<?>, Object> row : rows) {
            Object[] values = fields.stream().map(row::get).toArray();
            insert = insert.values(values);
        }

        return insert.returning(returnFields).fetchMaps();
    }

    public List<Map<String, Object>> insertReturning(ObjectMetadata objectMetadata, List<Map<String, Object>> values) {
        List<Map<Field<?>, Object>> fieldValues = values.stream().map(objectMap -> getFieldValues(objectMetadata, objectMap)).collect(Collectors.toList());
        return insertReturning(objectMetadata, fieldValues, objectMetadata.getFieldAlias());
    }

    public List<Map<String, Object>> insertReturningInsertedRows(ObjectMetadata objectMetadata, List<Map<String, Object>> values, String conflictOnConstraintName) {
        List<Map<Field<?>, Object>> rows = values.stream().map(objectMap -> getFieldValues(objectMetadata, objectMap)).collect(Collectors.toList());
        if (rows.isEmpty()) {
            return Utils.CL.newArrayList();
        }
        List<Field<?>> fields = new ArrayList<>(rows.getFirst().keySet());
        var insert = dsl.insertInto(objectMetadata.getTable()).columns(fields);

        for (Map<Field<?>, Object> row : rows) {
            Object[] insertValues = fields.stream().map(row::get).toArray();
            insert = insert.values(insertValues);
        }

        return insert.onConflictOnConstraint(DSL.name(conflictOnConstraintName))
                .doNothing()
                .returning(objectMetadata.getFieldAlias())
                .fetchMaps();
    }

    public Map<String, Object> updateById(ObjectMetadata meta, Object id, Map<String, Object> values) {
        Attribute attribute = meta.getIdAttribute();
        Field<Object> fieldId = (Field<Object>) attribute.getField();
        Object idValueCasted = attribute.cast(id);
        return updateReturning(meta, values, fieldId.eq(idValueCasted), meta.getFieldAlias());
    }

    public Map<String, Object> updateReturning(ObjectMetadata objectMetadata, Map<String, Object> values, Condition where, List<Field<?>> returnFields) {
        Map<Field<?>, Object> map = buildMapUpdateFields(objectMetadata, values, where);
        //Update new version and update where condition
        Condition versionCondition = updateOrGetVersionCondition(objectMetadata, map);

        Map<String, Object> affected = dsl.update(objectMetadata.getTable())
                .set(map)
                .where(where)
                .and(versionCondition)
                .returning(returnFields)
                .fetchOneMap();

        if (Utils.CL.isEmpty(affected)) {
            throw CommonErrors.optimisticLockingFailureException(objectMetadata.getName(), "Version conflict or record not found");
        }

        return affected;
    }

    public int update(ObjectMetadata objectMetadata, Map<String, Object> values, Condition where) {
        Map<Field<?>, Object> map = buildMapUpdateFields(objectMetadata, values, where);
        //Update new version and update where condition
        Condition versionCondition = updateOrGetVersionCondition(objectMetadata, map);

        int affected = dsl.update(objectMetadata.getTable())
                .set(map)
                .where(where)
                .and(versionCondition)
                .execute();

        if (affected == 0) {
            throw CommonErrors.optimisticLockingFailureException(objectMetadata.getName(), "Version conflict or record not found");
        }

        return affected;
    }

    private Map<Field<?>, Object> buildMapUpdateFields(ObjectMetadata objectMetadata, Map<String, Object> values, Condition where) {
        if (where == null) {
            throw CommonErrors.missingWhereConditionException("WHERE condition must not be null");
        }

        if (where.equals(org.jooq.impl.DSL.noCondition())) {
            throw CommonErrors.missingWhereConditionException("WHERE condition must not be empty");
        }

        Map<Field<?>, Object> map = new LinkedHashMap<>();
        values.forEach((k, v) -> {
            Attribute attribute = objectMetadata.getAttributeByName(k);
            if (attribute != null) {
                Field<?> field = attribute.getField();
                map.put(field, attribute.cast(v));
            }
        });

        return map;
    }

    /**
     * Deletes a record by its identifier.
     *
     * <p>Builds condition from primary key and delegates to delete method.</p>
     *
     * @param meta metadata definition
     * @param id record identifier
     * @param values additional fields (audit, flags)
     * @return affected rows
     */
    public int deleteById(ObjectMetadata meta, Object id, Map<String, Object> values) {
        if (id == null) {
            throw CommonErrors.required("id", "Id must not be null");
        }

        Attribute attribute = meta.getIdAttribute();
        Field<Object> idField = (Field<Object>) attribute.getField();
        Object idCasted = attribute.cast(id);
        Condition condition = idField.eq(idCasted);

        return delete(meta, values, condition);
    }

    /**
     * Performs a conditional delete operation.
     *
     * <p>Supports soft delete using metadata configuration.</p>
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Validates WHERE condition</li>
     *   <li>Maps input values to fields</li>
     *   <li>Applies type casting</li>
     *   <li>Ensures only non-deleted records are affected</li>
     * </ul>
     *
     * <p><b>Soft Delete:</b></p>
     * <ul>
     *   <li>Updates record instead of physical delete</li>
     *   <li>Uses <code>isDeleted</code> flag</li>
     * </ul>
     *
     * @param meta metadata definition
     * @param values audit/update fields
     * @param condition delete condition
     * @return affected rows
     */
    public int delete(ObjectMetadata meta, Map<String, Object> values, Condition condition) {

        if (condition == null || condition.equals(DSL.noCondition())) {
            throw CommonErrors.missingWhereConditionException("DELETE must have WHERE condition");
        }

        Map<Field<?>, Object> updateFieldMap = new LinkedHashMap<>();
        Utils.CL.emptyIfNull(values).forEach((k, v) -> {
            Attribute attribute = meta.getAttributeByName(k);
            if (attribute != null) {
                Field<?> field = attribute.getField();
                updateFieldMap.put(field, attribute.cast(v));
            }
        });

        Condition versionCondition = updateOrGetVersionCondition(meta, updateFieldMap);

        int affected = dsl.update(meta.getTable())
                .set(updateFieldMap)
                .where(condition)
                .and(versionCondition)
                .execute();
        if (affected == 0) {
            throw CommonErrors.optimisticLockingFailureException(meta.getName(), "Version conflict or record not found");
        }
        return affected;
    }

    /**
     * Permanently deletes a record.
     *
     * <p>This bypasses all soft delete and audit mechanisms.
     *
     * @param meta metadata definition
     * @param id record identifier
     * @return affected rows count
     */
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

    /**
     * Retrieves a record by identifier.
     *
     * @param meta metadata definition
     * @param id record identifier
     * @param returnFields optional projection fields
     * @return record result or null
     */
    public Map<String, Object> findById(ObjectMetadata meta, Object id, List<String> returnFields) {
        if (id == null) {
            throw CommonErrors.required("id", "Id must not be null");
        }
        ObjectFilterRequest objectFilterRequest = ObjectFilterRequest
                .builder()
                .objectInfo(ObjectFilterRequest.ObjectInfo.of(meta.getName()))
                .returnFields(returnFields)
                .filters(FilterGroup
                        .builder()
                        .operator(LogicalOperator.AND)
                        .conditions(Utils.CL.newArrayList(FilterCondition.create(Constants.OBJECT_PK, FilterOperator.EQUALS, id)))
                        .build())
                .build();
        PageResponse<Map<String, Object>> result = filter(meta, objectFilterRequest);

        return Utils.CL.getFirst(result.getContents());
    }

    public Map<String, Object> findById(ObjectMetadata meta, Object id) {
        if (id == null) {
            throw CommonErrors.required("id", "Id must not be null");
        }
        ObjectFilterRequest objectFilterRequest = ObjectFilterRequest
                .builder()
                .objectInfo(ObjectFilterRequest.ObjectInfo.of(meta.getName()))
                .returnFields(meta.getFieldNames())
                .filters(FilterGroup
                        .builder()
                        .operator(LogicalOperator.AND)
                        .conditions(Utils.CL.newArrayList(FilterCondition.create(Constants.OBJECT_PK, FilterOperator.EQUALS, id)))
                        .build())
                .build();
        PageResponse<Map<String, Object>> result = filter(meta, objectFilterRequest);

        return Utils.CL.getFirst(result.getContents());
    }

    public List<Map<String, Object>> findAllByIds(ObjectMetadata meta, List<Object> ids, List<String> returnFields) {
        if (Utils.CL.isEmpty(ids)) {
            return Utils.CL.newArrayList();
        }
        ObjectFilterRequest objectFilterRequest = ObjectFilterRequest
                .builder()
                .objectInfo(ObjectFilterRequest.ObjectInfo.of(meta.getName()))
                .returnFields(returnFields)
                .filters(FilterGroup
                        .builder()
                        .operator(LogicalOperator.AND)
                        .conditions(Utils.CL.newArrayList(FilterCondition.create(Constants.OBJECT_PK, FilterOperator.IN, ids)))
                        .build())
                .build();
        PageResponse<Map<String, Object>> result = filter(meta, objectFilterRequest);

        return result.getContents();
    }

    public List<Map<String, Object>> findAllByIds(ObjectMetadata meta, List<Object> ids) {
        if (Utils.CL.isEmpty(ids)) {
            return Utils.CL.newArrayList();
        }
        ObjectFilterRequest objectFilterRequest = ObjectFilterRequest
                .builder()
                .objectInfo(ObjectFilterRequest.ObjectInfo.of(meta.getName()))
                .returnFields(meta.getFieldNames())
                .filters(FilterGroup
                        .builder()
                        .operator(LogicalOperator.AND)
                        .conditions(Utils.CL.newArrayList(FilterCondition.create(Constants.OBJECT_PK, FilterOperator.IN, ids)))
                        .build())
                .build();
        PageResponse<Map<String, Object>> result = filter(meta, objectFilterRequest);

        return result.getContents();
    }

    /**
     * Handles optimistic locking version control.
     *
     * <p>If version field exists:</p>
     * <ul>
     *   <li>Validates current version</li>
     *   <li>Increments version automatically</li>
     *   <li>Returns condition for concurrency check</li>
     * </ul>
     *
     * @param meta metadata definition
     * @param fields update fields
     * @return version condition or no-op
     */
    private Condition updateOrGetVersionCondition(ObjectMetadata meta, Map<Field<?>, Object> fields) {
        Attribute versionAttr = meta.getVersionAttribute();
        if (versionAttr != null) {
            Field<?> versionField = versionAttr.getField();
            Object versionValueObject = fields.get(versionField);
            if(versionValueObject == null) {
                throw CommonErrors.required("version", "Version must not be null");
            }
            if(versionValueObject instanceof Number currentVersion) {
                fields.put(versionField, currentVersion.longValue() + 1);
                return ((Field<Object>) versionField).eq(currentVersion.longValue());
            }
        }
        return DSL.noCondition();
    }
}