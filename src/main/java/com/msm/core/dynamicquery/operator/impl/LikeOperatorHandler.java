package com.msm.core.dynamicquery.operator.impl;

import com.msm.core.dynamicquery.FieldResolver;
import com.msm.core.dynamicquery.operator.ConditionUtils;
import com.msm.core.dynamicquery.operator.OperatorHandler;
import com.msm.core.exceptions.CommonErrors;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

public class LikeOperatorHandler implements OperatorHandler {

    @Override
    public Condition handle(ObjectMetadata objectMetadata, FilterCondition condition) {
        String fieldName = FieldResolver.pathAsFieldName(condition.getField());
        Attribute attribute = objectMetadata.getAttributeByName(fieldName);
        if(attribute == null) {
            throw CommonErrors.fieldNotFoundException(fieldName, fieldName + " not found");
        }
        Object value = condition.getValue();
        if (value == null) {
            return DSL.noCondition();
        }

        if(attribute.isJsonField()) {
            Field<String> field = (Field<String>) FieldResolver.resolve(objectMetadata, condition.getField());
            return ConditionUtils.unaccentLike(field, value.toString());
        }

        if (!attribute.getJavaType().isTypeOrSubTypeOf(String.class)) {
            return DSL.noCondition();
        }

        Field<String> fieldObj = (Field<String>) attribute.getField();

        return ConditionUtils.unaccentLike(fieldObj, value.toString());
    }
}