package com.msm.core.filter.operator;

import com.msm.core.filter.DataTypeUtils;
import com.msm.core.filter.domain.FilterCondition;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.Objects;
import java.util.function.Function;

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
        if (value == null) return null;
        if (targetType.isInstance(value)) return (T) value;

        if (targetType.isEnum()) {
            return (T) Enum.valueOf((Class<? extends Enum>) targetType, value.toString());
        }
        Function<String, ?> fn = DataTypeUtils.getCastFunction(targetType);

        if (Objects.nonNull(fn)) {
            return (T) fn.apply(value.toString());
        }

        throw new IllegalArgumentException("Cannot cast value '" + value + "' to type " + targetType.getName());
    }

    protected final IllegalArgumentException typeError(String operator, Path<?> path, FilterCondition c) {
        return new IllegalArgumentException(operator + " not supported for field '" + c.getField() + "' of type " + path.getType().getSimpleName());
    }
}

