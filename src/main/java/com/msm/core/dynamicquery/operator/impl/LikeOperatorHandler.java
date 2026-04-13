package com.msm.core.dynamicquery.operator.impl;

import com.msm.core.dynamicquery.operator.AbstractOperatorHandler;
import com.msm.core.filter.domain.FilterCondition;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

public class LikeOperatorHandler extends AbstractOperatorHandler {

    @Override
    protected Condition doHandle(Field<?> field, FilterCondition condition) {

        Object value = condition.getValue();
        if (value == null) {
            return DSL.noCondition();
        }

        if (!String.class.equals(field.getDataType().getType())) {
            return DSL.noCondition();
        }
        Field<String> fieldObj = field.cast(String.class);
        return unaccentLike(fieldObj, value.toString());
    }

    public static Condition unaccentLike(Field<String> field, String keyword) {

        String pattern = "%" + keyword.toLowerCase() + "%";
        Field<String> left = DSL.function("unaccent", String.class, DSL.lower(field));
        Field<String> right = DSL.function("unaccent", String.class, DSL.inline(pattern));

        return left.like(right);
    }
}