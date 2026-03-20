package com.msm.core.validate;

import com.msm.core.commons.Utils;
import com.msm.core.validate.domain.AttributeRuleEntry;
import com.msm.core.validate.domain.AttributeValidationRuleDto;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
public class ReferenceGraphObjectBuilder implements ReferenceGraph<Map<String, Object>> {
    private final InternalObjectLoader loader;

    @Override
    public Map<String, Object> build(String rootObject, AttributeRuleEntry ruleEntry, Map<String, Object> factsData) {
        Map<String, Object> context = new LinkedHashMap<>();
        context.put(Utils.STR.uncapitalize(rootObject), factsData);
        List<AttributeValidationRuleDto.ReferenceMetadataDto> refs = Utils.CL.emptyIfNull(ruleEntry.getReferenceMetadata());

        Map<String, Object> cache = new LinkedHashMap<>();

        for (AttributeValidationRuleDto.ReferenceMetadataDto ref : refs) {
            context.put(ref.getSourceAttribute(),
                    new LazyObjectRef(() -> {
                        Map<String, Object> target = loader.loadObject(ruleEntry);
                        cache.put(ref.getSourceAttribute(), target);
                        return ValidationFactsBuilder.toAttributeWithCamelCase(target);
                    })
            );
        }
        return context;
    }

    public static Object getPropertyValue(Map<String, Object> objectAttributeMap, String propertyName) {
        return objectAttributeMap.get(propertyName);
    }
}
