package com.msm.core.validate.attr;

import com.msm.core.commons.ValueConvertFactory;
import com.msm.core.validate.domain.Attribute;

import java.util.UUID;

public class UuidTypeHandler implements ValueValidationHandler {

    @Override
    public String supportType() {
        return "UUID";
    }

    @Override
    public boolean isValid(Attribute attribute, Object o) {
        UUID val = ValueConvertFactory.convert(UUID.class, o);
        return true;
    }
}
