package com.msm.core.validate.validation.impl;

import com.msm.core.commons.DataConvertFactory;
import com.msm.core.validate.domain.Attribute;

import java.util.Objects;

public class StringValidate implements SimpleAttributeValidator {
    @Override
    public boolean isValid(Attribute attr, Object value) {
        if(Objects.isNull(value)) {
            return true;
        }
        boolean isValid = true;
        String text = DataConvertFactory.convert(String.class, value);
        if(Objects.nonNull(attr.getRegex())) {
            isValid = text.matches(attr.getRegex());
        }
        return isValid && (Objects.isNull(attr.getMaxLength()) || text.length() <= attr.getMaxLength());
    }
}
