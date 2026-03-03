package com.msm.core.filter.operator.impl;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;

public class StringExpressions {
    private StringExpressions() {}

    private BooleanExpression iLike(Path<?> path, Object value) {
        StringExpression base = Expressions.stringPath(path.getMetadata());
        StringExpression left = Expressions.stringTemplate("CAST(unaccent({0}) AS text)", base);
        return Expressions.booleanTemplate("{0} ILIKE {1}", left, Expressions.constant("%" + value.toString() + "%"));
    }
}
