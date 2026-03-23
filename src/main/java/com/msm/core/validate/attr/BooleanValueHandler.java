package com.msm.core.validate.attr;

import com.msm.core.validate.domain.Attribute;

import java.util.Objects;

public final class BooleanValueHandler implements ValueValidationHandler {

    @Override
    public String supportType() {
        return "Boolean";
    }

    @Override
    public boolean isValid(Attribute attribute, Object value) {
        if(Objects.isNull(value)){
            return true;
        }
        String val = value.toString().toLowerCase();
        return val.equals("true") || val.equals("false");
    }

}