package com.msm.core.validate.attr;

import com.msm.core.metadata.Attribute;
import com.msm.core.validate.attr.rules.AttributeSimpleRuleFactory;

import java.util.Objects;

public final class StringValueHandler implements ValueValidationHandler {

    @Override
    public String supportType() {
        return "String";
    }

    @Override
    public boolean isValid(Attribute attribute, Object value) {
        if(Objects.isNull(value)) {
            return true;
        }
        return AttributeSimpleRuleFactory.validate(attribute, value);
    }
}
