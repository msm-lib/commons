package com.msm.core.dynamicquery;

import com.msm.core.commons.Utils;
import com.msm.core.exceptions.Errors;
import com.msm.core.filter.domain.*;
import com.msm.core.filter.domain.pageable.SortDirection;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DynamicQueryService {

    private final DSLContext dsl;

    public int insert(Table<?> table, Map<String, Object> values) {

        Map<Field<?>, Object> map = new LinkedHashMap<>();
        values.forEach((k, v) -> {
            Field<?> field = resolve(table, k);
            map.put(field, v);
        });

        return dsl.insertInto(table)
                .set(map)
                .execute();
    }

    public Map<String, Object> insertReturning(Table<?> table, Map<String, Object> values, List<Field<?>> returnField) {

        Map<Field<?>, Object> map = new LinkedHashMap<>();
        values.forEach((k, v) -> {
            map.put(resolve(table, k), v);
        });

        return dsl.insertInto(table)
                .set(map)
                .returning(returnField)
                .fetchOneMap();
    }

    public int update(Table<?> table, Map<String, Object> values, Condition where) {

        Map<Field<?>, Object> map = new LinkedHashMap<>();
        values.forEach((k, v) -> {
            Field<?> field = resolve(table, k);
            map.put(field, v);
        });

        return dsl.update(table)
                .set(map)
                .where(where)
                .execute();
    }

    public int delete(Table<?> table, Condition condition) {

        return dsl.deleteFrom(table)
                .where(condition)
                .execute();
    }

    public Field<?> resolve(Table<?> table, String path) {
        String[] parts = path.split("\\.");
        String columnField = Utils.STR.toSnakeCase(parts[0]);
        Field<?> base = table.field(columnField);

        if (parts.length == 1) {
            return base;
        }

        throw Errors.unsupported("Unsupported field: " + path);
    }

    public <T> PageResponse<T> query(ObjectFilterRequest request) {
        resolveSearchFilter(request);
        Table<?> table = TableMetadataFactory.getTable(request.getObjectInfo().getName());

        Condition condition = FilterBuilder.build(request.getFilters(), table);
        SelectConditionStep<Record> query = dsl
                .select(buildFields(request, table))
                .from(table)
                .where(condition);

        applySorting(query, request, table);
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

    private List<SelectField<?>> buildFields(ObjectFilterRequest request, Table<?> table) {
        if (request.getReturnFields() == null || request.getReturnFields().isEmpty()) {
            return Utils.CL.newArrayList((SelectField<?>) DSL.asterisk());
        }

        return request.getReturnFields()
                .stream()
                .map(f -> getField(table, f))
                .collect(Collectors.toList());
    }

    private SelectField<?> getField(Table<?> table, String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        String alias = parts[0];
        if (parts.length > 1) {
            alias = String.join(".", parts);
        }

        Field<?> field = FieldResolver.resolve(table, fieldPath);

        return field.as(alias);
    }

    private void applySorting(SelectConditionStep<org.jooq.Record> query,
                              ObjectFilterRequest request,
                              Table<?> table) {

        if (Objects.isNull(request.getPageRequest())) return;
        var sorts = request.getPageRequest().getSorts();
        if (Objects.isNull(sorts)) return;

        List<SortField<?>> sortFields = sorts.stream()
                .map(s -> {
                    Field<?> field = FieldResolver.resolve(table, s.getAttribute());
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

    private Condition buildSearch(SearchRequest search, Table<?> table) {
        if (search == null || search.getKeyword() == null) {
            return DSL.noCondition();
        }

        List<Condition> conditions = search.getFields().stream()
                .map(f -> table.field(f, String.class)
                        .likeIgnoreCase("%" + search.getKeyword() + "%"))
                .collect(Collectors.toList());

        return DSL.or(conditions);
    }

    private FilterGroup toFilterGroup(SearchRequest searchRequest) {
        if(Objects.nonNull(searchRequest)) {
            List<String> fields = searchRequest.getFields();
            if(Utils.CL.isNotEmpty(fields) && Utils.STR.isNotBlank(searchRequest.getKeyword())) {
                FilterGroup.FilterGroupBuilder filterGroup = FilterGroup.builder();
                filterGroup.operator(LogicalOperator.OR);
                List<FilterObject> filterConditionList = fields.stream().map(field -> (FilterObject)FilterCondition
                        .builder()
                        .field(field)
                        .operator(FilterOperator.LIKE)
                        .value(searchRequest.getKeyword())
                        .build()).toList();

                filterGroup.conditions(filterConditionList);
                return filterGroup.build();
            }
        }
        return  null;
    }

    private void resolveSearchFilter(ObjectFilterRequest filter) {
        FilterGroup searchFilterGroup = toFilterGroup(filter.getSearch());
        if(Objects.nonNull(searchFilterGroup)) {
            FilterGroup currentFilterGroup = filter.getFilters();
            if(Objects.isNull(currentFilterGroup)) {
                filter.setFilters(searchFilterGroup);
            } else {
                List<FilterObject> filterConditionList = currentFilterGroup.getConditions();
                if(Utils.CL.isEmpty(filterConditionList)) {
                    filter.setFilters(searchFilterGroup);
                } else {
                    FilterGroup newSearchFilterGroup = FilterGroup.builder().operator(LogicalOperator.AND).conditions(Utils.CL.newArrayList(searchFilterGroup)).build();
                    currentFilterGroup.getConditions().add(newSearchFilterGroup);
                }
            }
        }
    }
}