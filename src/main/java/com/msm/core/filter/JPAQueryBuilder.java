package com.msm.core.filter;

import com.msm.core.commons.Utils;
import com.msm.core.filter.domain.JoinKey;
import com.msm.core.filter.domain.ReferenceJoinMetadata;
import com.msm.core.filter.domain.pageable.Pagination;
import com.msm.core.filter.join.ReferenceJoinCache;
import com.msm.core.filter.join.ReferenceJoinMetadataRegistry;
import com.msm.core.filter.join.ReferenceJoinResolver;
import com.msm.core.filter.sort.SortBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unchecked"})
public class JPAQueryBuilder<T> {
    private JPAQueryFactory queryFactory;
    private PathBuilder<?> root;
    private Predicate predicate;
    private ReferenceJoinResolver joinResolver;
    private List<Expression<?>> selectExpr;
    private Pagination pagination;

    public JPAQueryBuilder(JPAQueryFactory queryFactory, PathBuilder<?> root, Predicate predicate, ReferenceJoinResolver joinResolver, List<Expression<?>> selectExpr, Pagination pagination) {
        this.queryFactory = queryFactory;
        this.root = root;
        this.predicate = predicate;
        this.joinResolver = joinResolver;
        this.selectExpr = selectExpr;
        this.pagination = pagination;
    }

    public static <T> JPAQueryBuilder<T> create(JPAQueryFactory queryFactory, PathBuilder<?> root, Predicate predicate, ReferenceJoinResolver joinResolver, List<Expression<?>> selectExpr, Pagination pagination) {
        return new JPAQueryBuilder<>(queryFactory, root, predicate, joinResolver, selectExpr, pagination);
    }

    public JPAQuery<T> selectQuery() {
        JPAQuery<T> query = (JPAQuery<T>) queryFactory
                .select(selectExpr.toArray(new Expression[0]))
                .from(root);
        applyJoin(query, root);
        if (Objects.nonNull(predicate)) {
            query.where(predicate);
        }

        List<OrderSpecifier<?>> orderSpecifiers = SortBuilder.build(pagination.getSorts(), root, joinResolver);

        if (!orderSpecifiers.isEmpty()) {
            query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]));
        }

        if (Objects.nonNull(pagination)) {
            query.offset(pagination.getPage());
            query.limit(pagination.getSize());
        }

        return query;
    }

    public Long count() {
        JPAQuery<Long> countQuery = queryFactory
                .select(root.count())
                .from(root);
        applyJoin(countQuery, root);

        if (Objects.nonNull(predicate)) {
            countQuery.where(predicate);
        }

        return Utils.defaultIfNull(countQuery.fetchOne(), () -> 0L);
    }

    public void applyJoin(JPAQuery<?> query, PathBuilder<?> root) {
        for (var entry : ReferenceJoinCache.entries()) {
            JoinKey key = entry.getKey();
            PathBuilder<?> target = entry.getValue();
            ReferenceJoinMetadata ref = ReferenceJoinMetadataRegistry.get(key.sourceType(), key.attribute());

            String alias = target.getMetadata().getName().substring(0, target.getMetadata().getName().lastIndexOf("_"));
            PathBuilder<?> source = new PathBuilder<>(key.sourceType(), alias);

            query.leftJoin(target).on(source.get(ref.sourceColumn()).eq(target.get(ref.targetColumn())));
        }
    }
}
