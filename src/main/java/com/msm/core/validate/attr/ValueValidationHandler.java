package com.msm.core.validate.attr;

import com.msm.core.metadata.Attribute;

public interface ValueValidationHandler {

    String supportType();

    boolean isValid(Attribute attribute, Object value);
}