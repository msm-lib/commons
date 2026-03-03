package com.msm.core.filter.domain;

public record ReferenceJoinMetadata(
        String name,                // "category"
        Class<?> sourceEntity,      // Program
        String sourceColumn,        // category_id
        Class<?> targetEntity,      // Category
        String targetColumn,        // id
        JoinType joinType
) {}