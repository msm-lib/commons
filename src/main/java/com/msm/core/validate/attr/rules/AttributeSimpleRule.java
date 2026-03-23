package com.msm.core.validate.attr.rules;

import com.msm.core.validate.domain.Attribute;

public interface AttributeSimpleRule {
    boolean supports(Attribute attribute);
    boolean validate(Attribute attribute, Object value);
}