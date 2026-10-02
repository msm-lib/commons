package com.msm.core.validate.attr.rules;

import com.msm.core.commons.Utils;
import com.msm.core.metadata.Attribute;

import java.util.Objects;

public class MinLengthSimpleRule implements AttributeSimpleRule {

    @Override
    public boolean supports(Attribute attribute) {
        return Objects.nonNull(attribute.getMinLength()) && attribute.getJavaType().isTypeOrSubTypeOf(String.class);
    }

    @Override
    public boolean validate(Attribute attribute, Object value) {
        String data = Utils.STR.defaultIfBlank(attribute.cast(value), () -> "");
        return data.length() >= attribute.getMinLength();
    }
}