package com.msm.core.validate.validation;

import com.msm.core.commons.Utils;
import com.msm.core.validate.LazyObjectProxy;
import com.msm.core.validate.ReferenceGraph;
import com.msm.core.validate.domain.AttributeRuleEntry;
import lombok.RequiredArgsConstructor;
import org.jeasy.rules.api.Facts;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class ValidationFactsBuilder {

    private final ReferenceGraph<Map<String, Object>> referenceGraphObject;

    public static List<Map<String, Object>> toAttributeWithCamelCase(List<Map<String, Object>> attributeMap) {
        List<Map<String, Object>> newResultMaps = new ArrayList<>();
        attributeMap.forEach(map -> {
            Map<String, Object> newResultMap = new HashMap<>();
            toCamelCase(map, newResultMap);
            newResultMaps.add(newResultMap);
        });
        attributeMap.clear();

        return newResultMaps;
    }

    public static Map<String, Object> toAttributeWithCamelCase(Map<String, Object> attributeMap) {
        Map<String, Object> newResultMap = new HashMap<>();
        toCamelCase(attributeMap, newResultMap);
        attributeMap.clear();

        return newResultMap;
    }

    private static void toCamelCase(Map<String, Object> factAttributes, Map<String, Object> result) {
        for (Map.Entry<String, Object> entry : factAttributes.entrySet()) {
            String attrClaimCase = Utils.STR.toCamelCaseUnderscore(entry.getKey());
            Object value = entry.getValue();
            if(value instanceof Map) {
                Map<String, Object> newResultMap = new HashMap<>();
                toCamelCase((Map<String, Object>) value, newResultMap);
                result.put(attrClaimCase, newResultMap);
            } else {
                result.put(attrClaimCase, value);
            }
        }
    }

    public Facts build(String rootObject, AttributeRuleEntry ruleEntry, Map<String, Object> requestDataMap) {
        Facts facts = new Facts();
        if ("REFERENCE".equals(ruleEntry.getAttribute().getAttributeType())) {
            Map<String, Object> contextMap = referenceGraphObject.build(rootObject, ruleEntry, requestDataMap);
            AtomicReference<Object> currentProxied = new AtomicReference<>();
            contextMap.forEach((key, value) -> {
                Object proxied = LazyObjectProxy.createProxy(value);
                if (Objects.nonNull(currentProxied.get())) {
                    ((Map<String, Object>) currentProxied.get()).put(key, proxied);
                } else {
                    facts.put(key, proxied);
                }
                currentProxied.set(proxied);
            });
        } else {
            facts.put(Utils.STR.uncapitalize(rootObject), requestDataMap);
        }

        return facts;
    }
}

