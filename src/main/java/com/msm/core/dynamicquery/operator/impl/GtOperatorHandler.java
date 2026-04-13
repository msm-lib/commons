package com.msm.core.dynamicquery.operator.impl;

import com.msm.core.dynamicquery.operator.AbstractOperatorHandler;
import com.msm.core.filter.domain.FilterCondition;
import org.jooq.Condition;
import org.jooq.Field;

@SuppressWarnings({"unchecked"})
public class GtOperatorHandler extends AbstractOperatorHandler {

    @Override
    protected Condition doHandle(Field<?> field, FilterCondition condition) {
        Field<Object> fieldObj = (Field<Object>) field;
        Object value = condition.getValue();
        return fieldObj.gt(value);
    }
}