package com.msm.core.filter;

import com.msm.core.filter.join.ReferenceJoinResolver;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;

public interface PredicateFactory {
    BooleanExpression create(Object condition, PathBuilder<?> root, ReferenceJoinResolver joinResolver);
}
