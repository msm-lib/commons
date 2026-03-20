package com.msm.core.validate.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class AttributeRuleEntry {
    private UUID validationRuleId;
    private String name;
    private Integer priority;
    private String errorMessage;
    private RuleEntry ruleEntry;
    private List<AttributeValidationRuleDto.ReferenceMetadataDto> referenceMetadata;
    private AttributeAndListValueDto attribute;
}
