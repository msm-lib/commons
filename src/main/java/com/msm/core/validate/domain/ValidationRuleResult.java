package com.msm.core.validate.domain;

import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationRuleResult {
    private List<ObjectValidateResult> validateResults;

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ObjectValidateResult {
        private boolean isValid;
        private Map<String, Object> attributes;
        private List<MessageError> attributeErrors;
    }
}
