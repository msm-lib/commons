package com.msm.core.validate;

import com.msm.core.validate.domain.AttributeRuleEntry;

import java.util.Map;

public interface ReferenceGraph<T> {
    T build(String rootObject, AttributeRuleEntry ruleEntry, Map<String, Object> factsData);
}
