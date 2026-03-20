package com.msm.core.filter.operator;

import com.msm.core.commons.DataConvertFactory;
import com.msm.core.filter.domain.FilterCondition;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractOperatorHandler implements OperatorHandler {

    @Override
    public final BooleanExpression handle(Path<?> path, Object value, FilterCondition condition) {
        validate(path, value, condition);
        return doHandle(path, value, condition);
    }

    protected void validate(Path<?> path, Object value, FilterCondition condition) {}
    protected abstract BooleanExpression doHandle(Path<?> path, Object value, FilterCondition condition);

    public static <T> T cast(Class<?> targetType, Object value) {
        return DataConvertFactory.convert(targetType, value);
    }

    protected final IllegalArgumentException typeError(String operator, Path<?> path, FilterCondition c) {
        return new IllegalArgumentException(operator + " not supported for field '" + c.getField() + "' of type " + path.getType().getSimpleName());
    }
}

