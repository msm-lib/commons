package com.msm.core.validate.attr.rules;

import com.msm.core.commons.ValueConvertFactory;
import com.msm.core.commons.GenericTypeResolverFactory;
import com.msm.core.validate.domain.Attribute;

import java.math.BigDecimal;
import java.util.Objects;

public class MinValueSimpleRule implements AttributeSimpleRule {
    @Override
    public boolean supports(Attribute attribute) {
        Class<?> aClass = GenericTypeResolverFactory.resolve(attribute.getFieldType()).getRawClass();
        return Objects.nonNull(attribute.getMinValue()) && Number.class.isAssignableFrom(aClass);
    }

    @Override
    public boolean validate(Attribute attribute, Object value) {
        String type = attribute.getFieldType();
        switch (type){
            case "Integer" -> {
                Integer val = ValueConvertFactory.convert(Integer.class, value);
                return val >= attribute.getMinValue();
            }
            case "Long" -> {
                Long val = ValueConvertFactory.convert(Long.class, value);
                return val >= attribute.getMinValue();
            }
            case "Double" -> {
                Double val = ValueConvertFactory.convert(Double.class, value);
                return val >= attribute.getMinValue();
            }
            case "BigDecimal" -> {
                BigDecimal bigDecimal = ValueConvertFactory.convert(BigDecimal.class, value);
                return bigDecimal.compareTo(BigDecimal.valueOf(attribute.getMinValue())) >= 0;
            }
        }

        return false;
    }
}
