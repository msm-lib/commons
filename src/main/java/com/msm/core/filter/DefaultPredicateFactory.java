package com.msm.core.filter;


import com.msm.core.filter.cache.PathCache;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.filter.domain.FilterGroup;
import com.msm.core.filter.domain.LogicalOperator;
import com.msm.core.filter.join.ReferenceJoinResolver;
import com.msm.core.filter.operator.OperatorFactory;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;

import java.util.ArrayList;
import java.util.List;

public class DefaultPredicateFactory implements PredicateFactory {
    @Override
    public BooleanExpression create(Object condition, PathBuilder<?> root, ReferenceJoinResolver joinResolver) {
        return build(condition, root, joinResolver);
    }

    private BooleanExpression build(Object condition, PathBuilder<?> root, ReferenceJoinResolver joinResolver) {
        if (condition == null) return null;

        if (condition instanceof FilterCondition fc) {
            return buildCondition(fc, root, joinResolver);
        }

        if (condition instanceof FilterGroup fg) {
            return buildGroup(fg, root, joinResolver);
        }

        throw new IllegalArgumentException("Unsupported filter condition type: " + condition.getClass());
    }

    private BooleanExpression buildGroup(
            FilterGroup group,
            PathBuilder<?> root,
            ReferenceJoinResolver joinResolver
    ) {
        if (group.getConditions() == null || group.getConditions().isEmpty()) {
            return null;
        }

        List<BooleanExpression> expressions = new ArrayList<>();

        for (Object c : group.getConditions()) {
            BooleanExpression exp = build(c, root, joinResolver);
            if (exp != null) {
                expressions.add(exp);
            }
        }

        if (expressions.isEmpty()) return null;

        BooleanExpression result = expressions.getFirst();

        for (int i = 1; i < expressions.size(); i++) {
            result = LogicalOperator.OR.equals(group.getOperator())
                    ? result.or(expressions.get(i))
                    : result.and(expressions.get(i));
        }

        return result;
    }

    private BooleanExpression buildCondition(FilterCondition c, PathBuilder<?> root, ReferenceJoinResolver joinResolver) {

        Path<?> path = PathCache.resolve(c.getField(), root, joinResolver, c.getJoinType());

        return OperatorFactory.get(c.getOperator()).handle(path, c.getValue(), c);
    }
}