package com.msm.core.dynamicquery.operator.impl;

import com.msm.core.dynamicquery.ConditionUtils;
import com.msm.core.dynamicquery.FieldResolver;
import com.msm.core.dynamicquery.operator.OperatorHandler;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

public class LikeOperatorHandler implements OperatorHandler {

    @Override
    public Condition handle(ObjectMetadata objectMetadata, FilterCondition condition) {
        FieldResolver.ResolvedField target = FieldResolver.resolveTarget(objectMetadata, condition.getField());
        Attribute attribute = target.attribute();
        Object value = condition.getValue();
        if (value == null) {
            return DSL.noCondition();
        }

        if(target.json()) {
            Field<String> field = (Field<String>) target.field();
            return ConditionUtils.unaccentLike(field, value.toString());
        }

        if (!attribute.getJavaType().isTypeOrSubTypeOf(String.class)) {
            return DSL.noCondition();
        }

        Field<String> fieldObj = (Field<String>) attribute.getField();

        return ConditionUtils.unaccentLike(fieldObj, value.toString());
    }
}