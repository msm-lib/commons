package com.msm.core.dynamicquery.operator.impl;

import com.msm.core.dynamicquery.ConditionUtils;
import com.msm.core.dynamicquery.FieldResolver;
import com.msm.core.dynamicquery.operator.OperatorHandler;
import com.msm.core.exceptions.CommonErrors;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Condition;

public class ContainsOneOfOperatorHandler implements OperatorHandler {

    @Override
    public Condition handle(ObjectMetadata objectMetadata, FilterCondition condition) {
        FieldResolver.ResolvedField target = FieldResolver.resolveTarget(objectMetadata, condition.getField());
        Attribute attribute = target.attribute();
        if (target.path()) {
            throw CommonErrors.unsupported(condition.getOperator().name(), "The " + condition.getOperator() + " operator is not supported for the JSON path " + condition.getField());
        }
        return ConditionUtils.buildArrayOverlapCondition(attribute, condition.getValue());
    }
}