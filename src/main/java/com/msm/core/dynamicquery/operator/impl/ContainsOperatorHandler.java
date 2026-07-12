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

public class ContainsOperatorHandler implements OperatorHandler {

    @Override
    public Condition handle(ObjectMetadata objectMetadata, FilterCondition condition) {
        String fieldName = FieldResolver.pathAsFieldName(condition.getField());
        Attribute attribute = objectMetadata.getAttributeByName(fieldName);
        if(attribute == null) {
            throw CommonErrors.fieldNotFoundException(fieldName, fieldName + " not found");
        }
        Object value = condition.getValue();
        if(!(value instanceof Collection<?>)) {
            return ConditionUtils.unaccentLike((Field<String>) attribute.getField(), value == null ? "" : value.toString());
        }
        return ConditionUtils.buildArrayContainsCondition(attribute, value);
    }
}