package com.msm.core.filter.domain;

public record FieldMetadata(
        String field,
        Class<?> javaType,
        boolean comparable,
        boolean stringLike,
        boolean isEnum,
        boolean jsonType
) {}
