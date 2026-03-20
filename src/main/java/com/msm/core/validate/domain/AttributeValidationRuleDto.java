package com.msm.core.validate.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeValidationRuleDto {
    private UUID id;
    private UUID attributeId;
    private String ruleType;
    private String ruleName;
    private String errorMessage;
    private RuleEntry ruleEntry;
    private Integer priority;
    private Boolean isActive;
    private List<ReferenceMetadataDto> referenceMetadata;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReferenceMetadataDto {
        private UUID id;
        private String sourceObject;
        private String sourceAttribute;
        private String targetObject;
        private String targetAttribute;
        private ApiConfigDto apiInfo;
        private UUID attributeId;
        private UUID validationRuleId;
    }
}
