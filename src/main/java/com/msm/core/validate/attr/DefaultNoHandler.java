package com.msm.core.validate.attr;

import com.msm.core.metadata.Attribute;

public class DefaultNoHandler implements ValueValidationHandler {

    @Override
    public String supportType() {
        return "default";
    }

    @Override
    public boolean isValid(Attribute attribute, Object value) {
        return true;
    }
}