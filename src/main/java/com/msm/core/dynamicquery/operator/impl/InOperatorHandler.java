package com.msm.core.dynamicquery.operator.impl;

import com.msm.core.dynamicquery.FieldResolver;
import com.msm.core.dynamicquery.operator.OperatorHandler;
import com.msm.core.exceptions.Errors;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Condition;
import org.jooq.Field;

import java.util.Collection;

@SuppressWarnings({"unchecked"})
public class InOperatorHandler implements OperatorHandler {

    @Override
    public Condition handle(ObjectMetadata objectMetadata, FilterCondition condition) {
        String fieldName = FieldResolver.pathAsFieldName(condition.getField());
        Attribute attribute = objectMetadata.getAttributeByName(fieldName);
        if(attribute == null) {
            throw Errors.fieldNotFoundException(fieldName + " not found");
        }
        Object value = condition.getValue();
        if(!(value instanceof Collection<?> valueCol)) {
            throw Errors.invalid("The " + condition.getOperator() + " operator is not supported by this value for field " + condition.getField());
        }

        if(attribute.isJsonField()) {
            Field<Object> field = (Field<Object>) FieldResolver.resolve(condition.getField(), objectMetadata);
            return field.in(value);
        }

        Field<Object> field = (Field<Object>) attribute.getField();
        return field.in(valueCol.stream().map(attribute::cast).toList());
    }
}