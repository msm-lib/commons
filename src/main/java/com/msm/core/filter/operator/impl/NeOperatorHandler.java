package com.msm.core.filter.operator.impl;

import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.filter.domain.FilterOperator;
import com.msm.core.filter.operator.AbstractOperatorHandler;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;

import java.util.Objects;

@SuppressWarnings({"rawtypes", "unchecked"})
public class NeOperatorHandler extends AbstractOperatorHandler {

    @Override
    protected BooleanExpression doHandle(Path<?> path, Object value, FilterCondition c) {

        if (!(path instanceof SimpleExpression<?> exp)) {
            throw typeError(FilterOperator.NE.name(), path, c);
        }

        if (Objects.nonNull(value)) {
            Object typedValue = cast(exp.getType(), value);
            return ((SimpleExpression) exp).ne(typedValue);
        }

        return exp.isNull();
    }
}