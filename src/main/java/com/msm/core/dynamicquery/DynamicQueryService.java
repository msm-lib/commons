package com.msm.core.dynamicquery;

import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;
import com.msm.core.dynamicquery.context.RequestContextHolder;
import com.msm.core.exceptions.Errors;
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
import org.jooq.InsertValuesStepN;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DSL;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked"})
@RequiredArgsConstructor
public class DynamicQueryService {

    private final DSLContext dsl;

    public <T> PageResponse<T> filter(ObjectMetadata objectMetadata, ObjectFilterRequest request) {
        SearchOrDefaultFilter.resolveSearchFilter(request);
        SearchOrDefaultFilter.addIsDeletedFilter(objectMetadata, request);
        Table<?> table = objectMetadata.getTable();

        Condition condition = FilterBuilder.build(request.getFilters(), objectMetadata);
        SelectConditionStep<Record> query = dsl
                .select(SelectBuilder.buildFields(request, objectMetadata))
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
            return PageResponse.of((List<T>) result, total, request.getPageRequest().getPage(), request.getPageRequest().getSize());
        }

        return (PageResponse<T>) PageResponse.of(result);
    }

    private void applySorting(SelectConditionStep<org.jooq.Record> query,
                              ObjectFilterRequest request,
                              ObjectMetadata objectMetadata) {

        if (Objects.isNull(request.getPageRequest())) return;
        List<Sort> sorts = request.getPageRequest().getSorts();
        if (Objects.isNull(sorts)) return;

        List<SortField<?>> sortFields = sorts.stream()
                .map(s -> {
                    Field<?> field = FieldResolver.resolve(s.getAttribute(), objectMetadata);
                    return SortDirection.ASC.name().equalsIgnoreCase(s.getDirection().name())
                            ? field.asc()
                            : field.desc();
                })
                .collect(Collectors.toList());

        query.orderBy(sortFields);
    }

    private void applyPaging(SelectConditionStep<org.jooq.Record> query,
                             ObjectFilterRequest request) {

        if (Objects.isNull(request.getPageRequest())) return;
        int size = request.getPageRequest().getSize();
        int offset = request.getPageRequest().getOffset();

        query.limit(size).offset(offset);
    }

    private Map<Field<?>, Object> getFieldValues(ObjectMetadata objectMetadata, Map<String, Object> values) {
        List<Attribute> attrs = objectMetadata.getAttributes();
        Map<Field<?>, Object> fieldValues = new HashMap<>();
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

    public List<UpdatableRecord<?>> buildRecords(ObjectMetadata meta, List<Map<Field<?>, Object>> rows) {
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

    public int[] batchInsert(ObjectMetadata objectMetadata, List<Map<String, Object>> values) {
        List<Map<Field<?>, Object>> fieldValues = values.stream().map(objectMap -> getFieldValues(objectMetadata, objectMap)).collect(Collectors.toList());
        List<UpdatableRecord<?>> records = buildRecords(objectMetadata, fieldValues);
        return dsl.batchInsert(records).execute();
    }

    public int[] insert(ObjectMetadata objectMetadata, List<Map<String, Object>> values) {
        return batchInsert(objectMetadata, values);
    }

    public int insert(ObjectMetadata objectMetadata, Map<String, Object> values) {
        Map<Field<?>, Object> fieldValues = getFieldValues(objectMetadata, values);
        return dsl.insertInto(objectMetadata.getTable())
                .set(fieldValues)
                .execute();
    }

    public Map<String, Object> insertReturning(ObjectMetadata objectMetadata, Map<String, Object> values, List<Field<?>> returnField) {
        Map<Field<?>, Object> fieldValues = getFieldValues(objectMetadata, values);
        return dsl.insertInto(objectMetadata.getTable())
                .set(fieldValues)
                .returning(returnField)
                .fetchOneMap();
    }

    public Map<String, Object> insertReturning(ObjectMetadata objectMetadata, Map<String, Object> values) {
        return insertReturning(objectMetadata, values, objectMetadata.getFieldAlias());
    }

    public int updateById(ObjectMetadata meta, Object id, Map<String, Object> values) {
        Attribute attribute = meta.getIdAttribute();
        Field<Object> fieldId = (Field<Object>) attribute.getField();
        Object idValueCasted = attribute.cast(id);

        return update(meta, values, fieldId.eq(idValueCasted));
    }

    public int update(ObjectMetadata objectMetadata, Map<String, Object> values, Condition where) {
        if (where == null) {
            throw Errors.missingWhereConditionException("WHERE condition must not be null");
        }

        if (where.equals(org.jooq.impl.DSL.noCondition())) {
            throw Errors.missingWhereConditionException("WHERE condition must not be empty");
        }

        Map<Field<?>, Object> map = new LinkedHashMap<>();
        values.forEach((k, v) -> {
            Attribute attribute = objectMetadata.getAttributeByName(k);
            if (attribute != null) {
                Field<?> field = attribute.getField();
                map.put(field, attribute.cast(v));
            }
        });

        //Update new version and update where condition
        Condition versionCondition = updateOrGetVersionCondition(objectMetadata, map);

        int affected = dsl.update(objectMetadata.getTable())
                .set(map)
                .where(where)
                .and(versionCondition)
                .execute();

        if (affected == 0) {
            throw Errors.optimisticLockingFailureException("Version conflict or record not found");
        }

        return affected;
    }

    public int deleteById(ObjectMetadata meta, Object id, Map<String, Object> values) {
        if (id == null) {
            throw Errors.invalid("Id must not be null");
        }

        Attribute attribute = meta.getIdAttribute();
        Field<Object> idField = (Field<Object>) attribute.getField();
        Object idCasted = attribute.cast(id);
        Condition condition = idField.eq(idCasted);

        return delete(meta, values, condition);
    }

    public int delete(ObjectMetadata meta, Map<String, Object> values, Condition condition) {

        if (condition == null || condition.equals(DSL.noCondition())) {
            throw Errors.missingWhereConditionException("DELETE must have WHERE condition");
        }

        Map<Field<?>, Object> updateFieldMap = new LinkedHashMap<>();
        Utils.CL.emptyIfNull(values).forEach((k, v) -> {
            Attribute attribute = meta.getAttributeByName(k);
            if (attribute != null) {
                Field<?> field = attribute.getField();
                updateFieldMap.put(field, attribute.cast(v));
            }
        });
        Attribute isDeleted = meta.getAttributeByName(Constants.IS_DELETED);
        Field<Object> isDeletedField = (Field<Object>) isDeleted.getField();

        return dsl.update(meta.getTable())
                .set(updateFieldMap)
                .where(condition)
                .and(isDeletedField.eq(Boolean.FALSE))
                .execute();
    }

    public int forceDeleteById(ObjectMetadata meta, Object id) {
        if (id == null) {
            throw Errors.invalid("Id must not be null");
        }

        Attribute attribute = meta.getIdAttribute();
        Field<Object> idField = (Field<Object>) attribute.getField();
        Object idCasted = attribute.cast(id);
        Condition condition = idField.eq(idCasted);
        return dsl.deleteFrom(meta.getTable())
                .where(condition)
                .execute();
    }

    public Map<String, Object> findById(ObjectMetadata meta, Object id, List<String> returnFields) {
        if (id == null) {
            throw Errors.invalid("Id must not be null");
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

    private Condition updateOrGetVersionCondition(ObjectMetadata meta, Map<Field<?>, Object> fields) {
        Attribute versionAttr = meta.getAttributeByName(Constants.VERSION);
        if (versionAttr != null) {
            Field<?> versionField = versionAttr.getField();
            Object versionValueObject = fields.get(versionField);
            if(versionValueObject instanceof Number currentVersion) {
                fields.put(versionField, currentVersion.longValue() + 1);
                return ((Field<Object>) versionField).eq(currentVersion.longValue());
            }
        }
        return DSL.noCondition();
    }

    private boolean isSoftDeleted(ObjectMetadata meta) {
        Attribute deletedAt = meta.getAttributeByName(Constants.DELETED_AT);
        Attribute deletedBy = meta.getAttributeByName(Constants.DELETED_BY);
        Attribute deletedById = meta.getAttributeByName(Constants.DELETED_BY_ID);
        Attribute isDeleted = meta.getAttributeByName(Constants.IS_DELETED);
        return isDeleted != null || deletedAt != null || deletedBy != null || deletedById != null;
    }
}