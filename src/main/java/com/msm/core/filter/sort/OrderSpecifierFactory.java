package com.msm.core.filter.sort;

import com.msm.core.filter.domain.pageable.Sort;
import com.msm.core.filter.domain.pageable.SortDirection;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.StringExpression;

public final class OrderSpecifierFactory {

    private OrderSpecifierFactory() {}

    public static OrderSpecifier<?> build(Path<?> path, Sort sort) {

        Expression<?> exp = ExpressionFactory.toSortable(path, sort);
        if (exp instanceof ComparableExpressionBase<?> cmp) {
            return sort.getDirection() == SortDirection.ASC ? cmp.asc() : cmp.desc();
        }
        if (exp instanceof StringExpression str) {
            return sort.getDirection() == SortDirection.ASC ? str.asc() : str.desc();
        }

        throw new IllegalStateException("Unreachable");
    }
}
