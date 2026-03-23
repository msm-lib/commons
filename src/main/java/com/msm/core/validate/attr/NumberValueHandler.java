package com.msm.core.validate.attr;

import com.msm.core.validate.attr.rules.AttributeSimpleRuleFactory;
import com.msm.core.validate.domain.Attribute;

import java.util.Objects;

public final class NumberValueHandler implements ValueValidationHandler {

    private final String dataType;
    public NumberValueHandler(String dataType){
        this.dataType = dataType;
    }

    @Override
    public String supportType() {
        return dataType;
    }

    @Override
    public boolean isValid(Attribute attribute, Object value) {
        if(Objects.isNull(value)) {
            return true;
        }
        return AttributeSimpleRuleFactory.validate(attribute, value);
    }
}