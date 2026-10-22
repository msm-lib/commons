package com.msm.core.dynamicquery.operator.impl;

import com.msm.core.dynamicquery.ConditionUtils;
import com.msm.core.dynamicquery.FieldResolver;
import com.msm.core.dynamicquery.operator.OperatorHandler;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Condition;
import org.jooq.Field;

@SuppressWarnings({"unchecked"})
public class EqOperatorHandler implements OperatorHandler {

    @Override
    public Condition handle(ObjectMetadata objectMetadata, FilterCondition condition) {
        FieldResolver.ResolvedField target = FieldResolver.resolveTarget(objectMetadata, condition.getField());
        Attribute attribute = target.attribute();
        Object value = condition.getValue();
        if(target.json()) {
            Field<Object> field = (Field<Object>) target.field();
            if (!target.path()) {
                return field.eq(value);
            }
            return value == null ? field.isNull() : field.eq(ConditionUtils.jsonText(value));
        }
        Field<Object> field = (Field<Object>) attribute.getField();
        if(value == null) {
            return field.isNull();
        }
        Object objectCasted = attribute.cast(value);
        return field.eq(objectCasted);
    }
}