package com.msm.core.validate.validation.impl;

import com.msm.core.validate.domain.Attribute;

import java.util.Map;

public interface SimpleAttributeValidator {
    boolean isValid(Attribute attr, Object value);
}
