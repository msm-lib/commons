package com.msm.core.validate.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.msm.core.commons.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeAndListValueDto {
    private UUID id;
    private String attributeType;
    private String label;
    private String key;
    private String fieldName;
    private Boolean isRequired;
    private Long maxLength;
    private Long maxValue;
    private Long minValue;
    private Long maxSize;
    private String defaultValue;
    private Boolean multiSelect;
    private String regex;
    private String formatValue;
    private String formula;
    private String formulaReturnType;

    @JsonIgnore
    private String valueOptions;

    private Integer decimalPrecision;
    private Boolean isVisible;
    private Boolean isSystem;
    private Integer sort;
    private UUID parentId;
    private Boolean isHierarchy = false;
    private Integer level;
    private String group;
    private String sectionGroup;
    private String sectionGroupName;
    private String dataType;
    private Boolean isEditable;
    private Boolean isDisplay;
    private List<AttributeValidationRuleDto> validationRules;
    private Object attributeListValues;
    private Integer version;
    @JsonIgnore
    private AttributeMetaData metaData;

    public String getFormula() {
        if(Objects.nonNull(metaData)) return metaData.getFormula();
        return formula;
    }

    public String getFormatValue() {
        if(Objects.nonNull(metaData)) return metaData.getFormatValue();
        return formatValue;
    }

    public String getFormulaReturnType() {
        if(Objects.nonNull(metaData)) return metaData.getFormulaReturnType();
        return formulaReturnType;
    }

    public Boolean getIsDisplay() {
        if(Objects.nonNull(metaData)) return Utils.O.defaultIfNull(metaData.getIsDisplay(), () -> Boolean.TRUE);
        return Boolean.TRUE;
    }
    public Boolean getIsEditable() {
        if(Objects.nonNull(metaData)) return Utils.O.defaultIfNull(metaData.getIsEditable(), () -> Boolean.TRUE);
        return Boolean.TRUE;
    }

    public String getFieldName() {
        return Utils.STR.toCamelCaseUnderscore(key);
    }
}
