package com.msm.core.filter.operator.impl;

import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.filter.operator.AbstractComparableHandler;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;

@SuppressWarnings({"rawtypes", "unchecked"})
public class LtOperatorHandler extends AbstractComparableHandler {
    @Override
    protected BooleanExpression doCompare(ComparableExpression exp, Object value, FilterCondition c) {
        Comparable<?> typed = cast(exp.getType(), value);
        return exp.lt(typed);
    }
}