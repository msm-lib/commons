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
public class LteOperatorHandler implements OperatorHandler {

    @Override
    public Condition handle(ObjectMetadata objectMetadata, FilterCondition condition) {
        FieldResolver.ResolvedField target = FieldResolver.resolveTarget(objectMetadata, condition.getField());
        Attribute attribute = target.attribute();
        Object value = condition.getValue();
        if(target.json()) {
            if (!target.path()) {
                return ((Field<Object>) target.field()).le(value);
            }
            return ConditionUtils.jsonComparable(target.field(), value).le(ConditionUtils.jsonOrderValue(value));
        }
        Object objectCasted = attribute.cast(value);
        Field<Object> field = (Field<Object>) attribute.getField();
        return field.le(objectCasted);
    }
}