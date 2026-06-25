package com.msm.core.dynamicquery.query;

import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;
import com.msm.core.dynamicquery.ConditionUtils;
import com.msm.core.dynamicquery.SelectBuilder;
import com.msm.core.exceptions.CommonErrors;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.filter.domain.FilterGroup;
import com.msm.core.filter.domain.FilterOperator;
import com.msm.core.filter.domain.LogicalOperator;
import com.msm.core.filter.domain.ObjectFilterRequest;
import com.msm.core.filter.domain.PageResponse;
import com.msm.core.metadata.ObjectMetadata;
import com.msm.core.security.SecurityConditionProvider;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.Table;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class DefaultDynamicFilterQuery implements DynamicFilterQuery {
    private final DSLContext dsl;
    private final SecurityConditionProvider securityConditionProvider;

    @Override
    public PageResponse<Map<String, Object>> lookup(ObjectMetadata meta, ObjectFilterRequest request) {
        SearchOrDefaultFilter.resolveSearchFilter(request);
        SearchOrDefaultFilter.addIsDeletedFilter(meta, request);
        Table<?> table = meta.getTable();
//        Condition securityCondition = securityConditionProvider.buildViewDataScopeCondition(meta);

        Condition condition = FilterBuilder.build(request.getFilters(), meta);
        SelectConditionStep<Record> query = dsl
                .select(SelectBuilder.buildFields(meta, request))
                .from(table)
                .where(condition);
//                .and(securityCondition);

        SortingApplier.apply(query, request, meta);
        PagingApplier.apply(query, request);
        List<Map<String, Object>> result = query.fetchMaps();
        if(Objects.nonNull(request.getPageRequest())) {
            Long total = dsl
                    .selectCount()
                    .from(table)
                    .where(condition)
//                    .and(securityCondition)
                    .fetchOne(0, Long.class);

            if (total == null) {
                total = 0L;
            }
            return PageResponse.of(result, total, request.getPageRequest().getPage(), request.getPageRequest().getSize());
        }

        return PageResponse.of(result);
    }


    @Override
    public PageResponse<Map<String, Object>> filter(ObjectMetadata meta, ObjectFilterRequest request) {
        SearchOrDefaultFilter.resolveSearchFilter(request);
        SearchOrDefaultFilter.addIsDeletedFilter(meta, request);
        Table<?> table = meta.getTable();
        Condition securityCondition = securityConditionProvider.buildViewDataScopeCondition(meta);

        Condition condition = FilterBuilder.build(request.getFilters(), meta);
        SelectConditionStep<Record> query = dsl
                .select(SelectBuilder.buildFields(meta, request))
                .from(table)
                .where(condition)
                .and(securityCondition);

        SortingApplier.apply(query, request, meta);
        PagingApplier.apply(query, request);
        List<Map<String, Object>> result = query.fetchMaps();
        if(Objects.nonNull(request.getPageRequest())) {
            Long total = dsl
                    .selectCount()
                    .from(table)
                    .where(condition)
                    .and(securityCondition)
                    .fetchOne(0, Long.class);

            if (total == null) {
                total = 0L;
            }
            return PageResponse.of(result, total, request.getPageRequest().getPage(), request.getPageRequest().getSize());
        }

        return PageResponse.of(result);
    }

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

    public List<Map<String, Object>> findByIds(ObjectMetadata meta, List<Object> ids, List<String> returnFields) {
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

    public List<Map<String, Object>> findByIds(ObjectMetadata meta, List<Object> ids) {
        return findByIds(meta, ids, meta.getFieldNames());
    }

    public List<Map<String, Object>> findByCondition(ObjectMetadata meta, Condition condition) {
        return findByCondition(meta, condition, meta.getFieldNames());
    }

    public List<Map<String, Object>> findByCondition(ObjectMetadata meta, Condition condition, List<String> returnFields) {
        ConditionUtils.requireWhereCondition(condition);
        Condition securityCondition = securityConditionProvider.buildViewDataScopeCondition(meta);

        return dsl
                .select(SelectBuilder.buildFields(meta, returnFields))
                .from(meta.getTable())
                .where(condition)
                .and(securityCondition)
                .fetchMaps();
    }

    public Map<String, Object> findOneByCondition(ObjectMetadata meta, Condition condition) {
        return findOneByCondition(meta, condition, meta.getFieldNames());
    }

    public Map<String, Object> findOneByCondition(ObjectMetadata meta, Condition condition, List<String> returnFields) {
        ConditionUtils.requireWhereCondition(condition);
        Condition securityCondition = securityConditionProvider.buildViewDataScopeCondition(meta);
        return dsl
                .select(SelectBuilder.buildFields(meta, returnFields))
                .from(meta.getTable())
                .where(condition)
                .and(securityCondition)
                .fetchOneMap();
    }
}
