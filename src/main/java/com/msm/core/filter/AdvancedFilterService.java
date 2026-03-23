package com.msm.core.filter;

import com.msm.core.commons.Utils;
import com.msm.core.filter.domain.*;
import com.msm.core.filter.domain.pageable.PageRequest;
import com.msm.core.filter.join.ReferenceJoinResolver;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;

@Data
@SuppressWarnings({"unchecked"})
public class AdvancedFilterService {

    protected final JPAQueryFactory queryFactory;
    protected final PredicateFactory predicateFactory;
    protected final ReferenceJoinResolver joinResolver;
    protected final EntityClassFactory entityClassFactory;

    public AdvancedFilterService(JPAQueryFactory queryFactory,
                                 PredicateFactory predicateFactory,
                                 EntityClassFactory entityClassFactory) {
        this.queryFactory = queryFactory;
        this.predicateFactory = predicateFactory;
        this.entityClassFactory = entityClassFactory;
        joinResolver = new ReferenceJoinResolver();
    }

    public <T> PageResponse<T> filter(ObjectFilterRequest objectFilterRequest) {
        resolveSearchFilter(objectFilterRequest);
        EntityPathBase<T> tEntityPathBase = EntityPathResolver.resolve((Class<T>) entityClassFactory.resolve(objectFilterRequest.getObjectInfo().getName()));
        PathBuilder<T> root = new PathBuilder<>(tEntityPathBase.getType(), tEntityPathBase.getMetadata());
        BooleanExpression predicate = predicateFactory.create(objectFilterRequest.getFilters(), root, joinResolver);
        Map<String, Expression<?>> selectExpr = DynamicSelectBuilder.build(objectFilterRequest.getReturnFields(), root, joinResolver);

        JPAQueryBuilder<Tuple> queryBuilder = JPAQueryBuilder.create(queryFactory, root, predicate, joinResolver, selectExpr.values().stream().toList(), objectFilterRequest.getPageRequest());
        JPAQuery<Tuple> dataQuery0 = queryBuilder.selectQuery();
        List<Tuple> tuples = dataQuery0.fetch();

        //Curent version not support query aggregate
//        Map<String, Object> aggregateResult = null;
//        if (objectFilter.getAggregate() != null && !objectFilter.getAggregate().isEmpty()) {
//            aggregateResult = executeAggregate(tEntityPathBase, objectFilter.getAggregate(), root, predicate);
//        }

        if(Objects.nonNull(objectFilterRequest.getPageRequest())) {
            long total = queryBuilder.count();
            return PageResponse.of((List<T>) ResultMapper.map(tuples, selectExpr), total, objectFilterRequest.getPageRequest().getPage(), objectFilterRequest.getPageRequest().getSize());
        }

        return PageResponse.of((List<T>) ResultMapper.map(tuples, selectExpr));
    }

    private Map<String, Object> executeAggregate(EntityPathBase<?> tEntityPathBase, List<AggregateRequest> aggregates, PathBuilder<?> root, BooleanExpression predicate) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Expression<?>> expressions = new ArrayList<>();

        for (AggregateRequest a : aggregates) {
            validateAggregate(a);
            Expression<?> exp = switch (a.getType()) {
                case COUNT -> root.get(a.getField()).count();
                case SUM -> root.getNumber(a.getField(), BigDecimal.class).sum();
            };

            expressions.add(exp);
        }

        Tuple tuple = queryFactory
                .select(expressions.toArray(new Expression[0]))
                .from(tEntityPathBase)
                .where(predicate)
                .fetchOne();

        for (int i = 0; i < aggregates.size(); i++) {
            AggregateRequest a = aggregates.get(i);
            String key = Optional.ofNullable(a.getAlias()).orElse(a.getType().name().toLowerCase() + "_" + a.getField());
            result.put(key, tuple.get(i, Object.class));
        }

        return result;
    }

    private void validateAggregate(AggregateRequest req) {
        if (req.getType() == AggregateType.SUM && !isNumericField(req.getField())) {
            throw new IllegalArgumentException("SUM is only allowed for numeric fields: " + req.getField());
        }
    }

    private boolean isNumericField(String field) {
        // Starter library: overrideable
        return true;
    }

    private FilterGroup toFilterGroup(SearchRequest searchRequest) {
        if(Objects.nonNull(searchRequest)) {
            List<String> fields = searchRequest.getFields();
            if(Utils.CL.isNotEmpty(fields) && Utils.STR.isNotBlank(searchRequest.getKeyword())) {
                FilterGroup.FilterGroupBuilder filterGroup = FilterGroup.builder();
                filterGroup.operator(LogicalOperator.AND);
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
                    filterConditionList.add(searchFilterGroup);
                }
            }
        }
    }
}
