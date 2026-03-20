package com.msm.core.validate.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.msm.core.commons.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeMetaData {
    private String formatValue;
    private String valuePath;
    private String objectFieldName;
    private Boolean isEditable;
    private Boolean isDisplay;
    private String formula;
    private String formulaReturnType;
    private String referenceAttributeName; // lookup to related object to get value by this field
    private String referenceObject; // lookup object
    private String referenceValuePath;
    private AttributeUiMetadata attributeUiMetadata;


    public String getReferenceAttributeName() {
        return Utils.O.defaultIfNull(referenceAttributeName, () -> "id");
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttributeUiMetadata {
        private List<String> displayAttributes;
        private List<String> searchAttributes;
        private List<String> pickerColumnAttributes;
    }
}
