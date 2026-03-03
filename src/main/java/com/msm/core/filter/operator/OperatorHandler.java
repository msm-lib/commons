package com.msm.core.filter.operator;

import com.msm.core.filter.domain.FilterCondition;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;

public interface OperatorHandler {
    BooleanExpression handle(Path<?> path, Object value, FilterCondition condition);
}