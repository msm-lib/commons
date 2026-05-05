package com.msm.core.filter.operator.impl;

import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.filter.operator.AbstractOperatorHandler;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;

public class JsonEqOperatorHandler extends AbstractOperatorHandler {
    @Override
    protected BooleanExpression doHandle(Path<?> path, Object value, FilterCondition condition) {
        return null;
    }

}
