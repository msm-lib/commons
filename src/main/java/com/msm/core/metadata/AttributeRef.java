package com.msm.core.metadata;

import lombok.Data;

@Data
public class AttributeRef {
    private String fieldName;
    private String objectRef;
    private String usageType = "Reference";
}
