package com.msm.core.validate.attr.rules;

import com.msm.core.metadata.Attribute;

import java.math.BigDecimal;
import java.util.Objects;

public class MaxValueSimpleRule implements AttributeSimpleRule {
    @Override
    public boolean supports(Attribute attribute) {
        return Objects.nonNull(attribute.getMaxValue()) && attribute.getJavaType().isTypeOrSubTypeOf(Number.class);
    }

    @Override
    public boolean validate(Attribute attribute, Object value) {
        String type = attribute.getFieldType();
        switch (type){
            case "Integer", "Long" -> {
                Integer val = attribute.cast(value);
                return val <= attribute.getMaxValue();
            }
            case "Double" -> {
                Double val = attribute.cast(value);
                double max = attribute.getMaxValue().doubleValue();
                return Double.compare(val, max) <=0;
            }
            case "BigDecimal" -> {
                BigDecimal val = attribute.cast(value);
                return val.compareTo(BigDecimal.valueOf(attribute.getMaxValue())) <= 0;
            }
        }

        return false;
    }
}
