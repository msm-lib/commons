package com.msm.core.validate.attr.rules;

import com.msm.core.validate.domain.Attribute;

import java.util.*;

public class AttributeSimpleRuleFactory {
    private final static List<AttributeSimpleRule> RULES = new ArrayList<>();
    static {
        register(new MaxLengthSimpleRule());
        register(new MinValueSimpleRule());
        register(new MaxValueSimpleRule());
        register(new RegexSimpleRule());
    }

    public static boolean validate(Attribute attribute, Object value) {
        for (AttributeSimpleRule rule : RULES) {
            if (!rule.supports(attribute)) {
                continue;
            }
            if (!rule.validate(attribute, value)) {
                return false;
            }
        }
        return true;
    }

    public static void register(AttributeSimpleRule attributeSimpleRule) {
        RULES.add(attributeSimpleRule);
    }
}
