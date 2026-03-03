package com.msm.core.filter;


import com.msm.core.filter.domain.AggregateRequest;
import com.msm.core.filter.domain.AggregateType;
import com.msm.core.filter.domain.ObjectFilter;
import com.msm.core.filter.domain.PagedResponse;
import com.msm.core.filter.join.ReferenceJoinResolver;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings({"unchecked"})
public class AdvancedFilterService {

    protected final JPAQueryFactory queryFactory;
    protected final PredicateFactory predicateFactory;
    protected final ReferenceJoinResolver joinResolver;
    protected final EntityClassRegistry entityClassRegistry;
    public AdvancedFilterService(JPAQueryFactory queryFactory,
                                    PredicateFactory predicateFactory,
                                    EntityClassRegistry entityClassRegistry) {
        this.queryFactory = queryFactory;
        this.predicateFactory = predicateFactory;
        this.entityClassRegistry = entityClassRegistry;
        joinResolver = new ReferenceJoinResolver();
    }

    public <T> PagedResponse<T> filter(ObjectFilter objectFilter) {
        EntityPathBase<T> tEntityPathBase = EntityPathResolver.resolve((Class<T>) entityClassRegistry.resolve(objectFilter.getObjectFilter().getName()));

        PathBuilder<T> root = new PathBuilder<>(tEntityPathBase.getType(), tEntityPathBase.getMetadata());
        BooleanExpression predicate = predicateFactory.create(objectFilter.getFilterGroup(), root, joinResolver);
        Map<String, Expression<?>> selectExpr = DynamicSelectBuilder.build(objectFilter.getReturnAttributes(), root, joinResolver);

        JPAQueryBuilder<Tuple> queryBuilder = JPAQueryBuilder.create(queryFactory, root, predicate, joinResolver, selectExpr.values().stream().toList(), objectFilter.getPageRequest());
        JPAQuery<Tuple> dataQuery0 = queryBuilder.selectQuery();
        List<Tuple> tuples = dataQuery0.fetch();

        Map<String, Object> aggregateResult = null;
        if (objectFilter.getAggregate() != null && !objectFilter.getAggregate().isEmpty()) {
            aggregateResult = executeAggregate(tEntityPathBase, objectFilter.getAggregate(), root, predicate);
        }

        if(Objects.nonNull(objectFilter.getPageRequest())) {
            long total = queryBuilder.count();
            int totalPages = (int) Math.ceil((double) total / objectFilter.getPageRequest().getSize());
            objectFilter.getPageRequest().setTotalPages(totalPages);
        }

        return PagedResponse.<T>builder()
                .records((List<T>) ResultMapper.map(tuples, selectExpr))
                .aggregate(aggregateResult)
                .pageRequest(objectFilter.getPageRequest())
                .build();
    }

//    public <T> List<T> filter(ObjectFilter objectFilter) {
//        EntityPathBase<T> tEntityPathBase = EntityPathResolver.resolve((Class<T>) entityClassRegistry.resolve(objectFilter.getObjectFilter().getName()));
//
//        PathBuilder<T> root = new PathBuilder<>(tEntityPathBase.getType(), tEntityPathBase.getMetadata());
//        BooleanExpression predicate = predicateFactory.create(objectFilter.getFilterGroup(), root, joinResolver);
//        Map<String, Expression<?>> selectExpr = DynamicSelectBuilder.build(objectFilter.getReturnAttributes(), root, joinResolver);
//
//        JPAQueryBuilder<Tuple> queryBuilder = JPAQueryBuilder.create(queryFactory, root, predicate, joinResolver, selectExpr.values().stream().toList(), objectFilter.getPagination());
//        JPAQuery<Tuple> dataQuery0 = queryBuilder.selectQuery();
//        List<Tuple> tuples = dataQuery0.fetch();
//
//        return (List<T>) ResultMapper.map(tuples, selectExpr);
//    }

    private Map<String, Object> executeAggregate(
            EntityPathBase<?> tEntityPathBase,
            List<AggregateRequest> aggregates,
            PathBuilder<?> root,
            BooleanExpression predicate
    ) {
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

            String key = Optional.ofNullable(a.getAlias())
                    .orElse(a.getType().name().toLowerCase()
                                    + "_" + a.getField()
                    );

            result.put(key, tuple.get(i, Object.class));
        }

        return result;
    }

    private void validateAggregate(AggregateRequest req) {

        if (req.getType() == AggregateType.SUM &&
                !isNumericField(req.getField())) {
            throw new IllegalArgumentException(
                    "SUM is only allowed for numeric fields: " + req.getField()
            );
        }
    }

    private boolean isNumericField(String field) {
        // Starter library: overrideable
        return true;
    }
}
