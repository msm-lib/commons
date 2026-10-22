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
        FieldResolver.ResolvedField target = FieldResolver.resolveTarget(objectMetadata, condition.getField());
        Attribute attribute = target.attribute();
        Object value = condition.getValue();
        if(!(value instanceof Collection<?>)) {
            // A JSON path is matched on its own text, not on the whole JSON column
            return ConditionUtils.unaccentLike((Field<String>) target.field(), value == null ? "" : value.toString());
        }
        if (target.path()) {
            throw CommonErrors.unsupported(condition.getOperator().name(), "The " + condition.getOperator() + " operator with a list of values is not supported for the JSON path " + condition.getField());
        }
        return ConditionUtils.buildArrayContainsCondition(attribute, value);
    }
}