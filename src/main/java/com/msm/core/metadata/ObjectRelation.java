package com.msm.core.metadata;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ObjectRelation {
    private String targetObject;
    private String foreignKeyAttribute;
    @Builder.Default
    private String targetAttribute = "id";
    private RelationalTypeEnum relationType;
}