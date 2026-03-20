package com.msm.core.validate.attr;

import com.msm.core.validate.AttributeTypeHandler;

public class StringTypeHandler implements AttributeTypeHandler {
    @Override
    public boolean supports(Class<?> t) {
        return false;
    }

    @Override
    public String dataType() {
        String.class.getName();
        return "String";
    }

    @Override
    public boolean isValid(Object value) {
        return false;
    }

    @Override
    public Object normalizeValue(Object value) {
        return null;
    }
}
