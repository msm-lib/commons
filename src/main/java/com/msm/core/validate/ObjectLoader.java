package com.msm.core.validate;


import com.msm.core.validate.domain.AttributeRuleEntry;

public interface ObjectLoader<T> {
    T loadObject(AttributeRuleEntry attributeRuleEntry);
}
