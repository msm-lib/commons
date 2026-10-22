package com.msm.core.dynamicquery.operator.impl;

import com.msm.core.dynamicquery.ConditionUtils;
import com.msm.core.dynamicquery.FieldResolver;
import com.msm.core.dynamicquery.operator.OperatorHandler;
import com.msm.core.exceptions.CommonErrors;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Condition;
import org.jooq.Field;

import java.util.Collection;

@SuppressWarnings({"unchecked"})
public class NotInOperatorHandler implements OperatorHandler {
    @Override
    public Condition handle(ObjectMetadata objectMetadata, FilterCondition condition) {
        FieldResolver.ResolvedField target = FieldResolver.resolveTarget(objectMetadata, condition.getField());
        Attribute attribute = target.attribute();
        Object value = condition.getValue();
        if(!(value instanceof Collection<?> valueCol)) {
            throw CommonErrors.unsupported(condition.getOperator().name(), "The " + condition.getOperator() + " operator is not supported by this value for field " + condition.getField());
        }
        if(target.json()) {
            Field<Object> field = (Field<Object>) target.field();
            return target.path() ? field.notIn(ConditionUtils.jsonTexts(valueCol)) : field.notIn(value);
        }
        Field<Object> field = (Field<Object>) attribute.getField();
        return field.notIn(valueCol.stream().map(attribute::cast).toList());
    }
}