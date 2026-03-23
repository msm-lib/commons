package com.msm.core.validate.attr.rules;

import com.msm.core.commons.ValueConvertFactory;
import com.msm.core.commons.GenericTypeResolverFactory;
import com.msm.core.commons.Utils;
import com.msm.core.validate.domain.Attribute;

import java.util.Objects;

public class MaxLengthSimpleRule implements AttributeSimpleRule {

    @Override
    public boolean supports(Attribute attribute) {
        Class<?> aClass = GenericTypeResolverFactory.resolve(attribute.getFieldType()).getRawClass();
        return Objects.nonNull(attribute.getMaxLength()) && aClass.equals(String.class);
    }

    @Override
    public boolean validate(Attribute attribute, Object value) {
        String data = Utils.STR.defaultIfBlank(ValueConvertFactory.convert(String.class, value), () -> "");
        return data.length() <= attribute.getMaxLength();
    }
}