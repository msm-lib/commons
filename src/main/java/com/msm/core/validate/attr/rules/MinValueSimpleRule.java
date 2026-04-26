package com.msm.core.validate.attr.rules;

import com.msm.core.metadata.Attribute;

import java.math.BigDecimal;
import java.util.Objects;

public class MinValueSimpleRule implements AttributeSimpleRule {
    @Override
    public boolean supports(Attribute attribute) {
        return Objects.nonNull(attribute.getMinValue()) && attribute.getJavaType().isTypeOrSubTypeOf(Number.class);
    }

    @Override
    public boolean validate(Attribute attribute, Object value) {
        String type = attribute.getFieldType();
        switch (type){
            case "Integer", "Long", "Double" -> {
                Integer val = attribute.cast(value);
                return val >= attribute.getMinValue();
            }
            case "BigDecimal" -> {
                BigDecimal bigDecimal = attribute.cast(value);
                return bigDecimal.compareTo(BigDecimal.valueOf(attribute.getMinValue())) >= 0;
            }
        }

        return false;
    }
}
