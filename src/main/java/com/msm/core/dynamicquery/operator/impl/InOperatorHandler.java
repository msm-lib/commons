package com.msm.core.dynamicquery.operator.impl;

import com.msm.core.dynamicquery.operator.AbstractOperatorHandler;
import com.msm.core.exceptions.Errors;
import com.msm.core.filter.domain.FilterCondition;
import org.jooq.Condition;
import org.jooq.Field;

import java.util.Collection;

@SuppressWarnings({"unchecked"})
public class InOperatorHandler extends AbstractOperatorHandler {

    @Override
    protected Condition doHandle(Field<?> field, FilterCondition condition) {
        Field<Object> fieldObj = (Field<Object>) field;
        Object value = condition.getValue();
        if(value instanceof Collection) {
            return fieldObj.in((Collection<?>) value);
        }
        throw Errors.invalid("The " + condition.getOperator() + " operator is not supported by this value for field " + field.getName());
    }
}