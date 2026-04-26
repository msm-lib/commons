package com.msm.core.dynamicquery.operator.impl;

import com.msm.core.dynamicquery.FieldResolver;
import com.msm.core.dynamicquery.operator.ConditionUtils;
import com.msm.core.dynamicquery.operator.OperatorHandler;
import com.msm.core.exceptions.Errors;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Condition;

public class ContainsOneOfOperatorHandler implements OperatorHandler {

    @Override
    public Condition handle(ObjectMetadata objectMetadata, FilterCondition condition) {
        String fieldName = FieldResolver.pathAsFieldName(condition.getField());
        Attribute attribute = objectMetadata.getAttributeByName(fieldName);
        if(attribute == null) {
            throw Errors.fieldNotFoundException(fieldName + " not found");
        }
        return ConditionUtils.buildArrayOverlapCondition(attribute, condition.getValue());
    }
}