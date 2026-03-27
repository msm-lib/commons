package com.msm.core.filter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldMetadata{
    private String field;
    private Class<?> javaType;
    private boolean comparable;
    private boolean isStringLike;
    private boolean isEnum;
    private boolean isJsonType;
    private boolean isEmbedded;
    private boolean isRelation;
}
