package com.msm.core.validate.attr;

import com.msm.core.metadata.Attribute;

public class UuidTypeHandler implements ValueValidationHandler {

    @Override
    public String supportType() {
        return "UUID";
    }

    @Override
    public boolean isValid(Attribute attribute, Object o) {
        attribute.cast(o);
        return true;
    }
}
