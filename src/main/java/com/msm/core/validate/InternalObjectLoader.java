package com.msm.core.validate;

import com.msm.core.commons.Utils;
import com.msm.core.validate.domain.AttributeRuleEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class InternalObjectLoader implements ObjectLoader<Map<String, Object>> {

    private final ApiClient apiClient;

    @Override
    public Map<String, Object> loadObject(AttributeRuleEntry attributeRuleEntry) {
        List<Map<String, Object>> object = getData(attributeRuleEntry);
        if(Utils.CL.isNotEmpty(object)) {
            return Utils.O.toMap(object.getFirst());
        }
        return new HashMap<>();
    }

    private List<Map<String, Object>> getData(AttributeRuleEntry attributeRuleEntry) {
        String api = attributeRuleEntry.getReferenceMetadata().getFirst().getApiInfo().getEndpoint();

        return apiClient.get(api, "POST", List.class);
    }
}
