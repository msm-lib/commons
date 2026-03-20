package com.msm.core.validate.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attribute {
    private String attributeType;
    private String fieldName;
    private Boolean isRequired;
    private String sourceAttribute;
    private Long maxLength;
    private Long maxValue;
    private Long minValue;
    private Long maxSize;
    private Object defaultValue;
    private Boolean multiSelect;
    private String regex;
    private String formatValue;
    private String formula;
    private Boolean isSystem;
    private String objectName;
}