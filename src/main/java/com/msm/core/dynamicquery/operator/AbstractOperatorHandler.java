package com.msm.core.dynamicquery.operator;

import com.msm.core.commons.ValueConvertFactory;
import com.msm.core.filter.domain.FilterCondition;
import org.jooq.Condition;
import org.jooq.Field;

public abstract class AbstractOperatorHandler implements OperatorHandler {

    @Override
    public Condition handle(Field<?> field, FilterCondition condition) {
        validate(field, condition);
        return doHandle(field, condition);
    }

    protected void validate(Field<?> field, FilterCondition condition) {}
    protected abstract Condition doHandle(Field<?> field, FilterCondition condition);

    public static <T> T cast(Class<?> targetType, Object value) {
        return ValueConvertFactory.convert(targetType, value);
    }

    protected final IllegalArgumentException typeError(String operator, Field<?> field, FilterCondition c) {
        return new IllegalArgumentException(operator + " not supported for field '" + c.getField() + "' of type " + field.getName());
    }
}

