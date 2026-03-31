package com.msm.core.filter.operator.impl;

import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.filter.domain.FilterOperator;
import com.msm.core.filter.operator.AbstractOperatorHandler;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"rawtypes", "unchecked"})
public class NotInOperatorHandler extends AbstractOperatorHandler {

    @Override
    protected BooleanExpression doHandle(Path<?> path, Object value, FilterCondition c) {

        if (!(path instanceof SimpleExpression<?> exp)) {
            throw typeError(FilterOperator.NOT_IN.name(), path, c);
        }

        if (Objects.nonNull(value)) {
            if (!(value instanceof Collection<?> values)) {
                throw new IllegalArgumentException("Not in operator requires a collection value");
            }
            List<?> converted = values
                    .stream()
                    .map(v -> cast(exp.getType(), v))
                    .toList();
            return ((SimpleExpression) path).notIn(converted);
        }

        return exp.isNull();
    }
}