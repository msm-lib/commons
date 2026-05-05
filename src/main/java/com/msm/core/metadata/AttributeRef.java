package com.msm.core.metadata;

import lombok.Data;

@Data
public class AttributeRef {
    private String fieldName;
    private String objectRef;
    private String usageType = "Reference";

    public AttributeRef(String fieldName, String objectRef) {
        this.fieldName = fieldName;
        this.objectRef = objectRef;
    }
    public AttributeRef(String fieldName, String objectRef, String usageType) {
        this.fieldName = fieldName;
        this.objectRef = objectRef;
        this.usageType = usageType;
    }

    public static AttributeRef of(String fieldName, String objectRef) {
        return new AttributeRef(fieldName, objectRef);
    }

    public static AttributeRef of(String fieldName, String objectRef, String usageType) {
        return new AttributeRef(fieldName, objectRef, usageType);
    }
}
