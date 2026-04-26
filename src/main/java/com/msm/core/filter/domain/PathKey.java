package com.msm.core.filter.domain;

import com.querydsl.core.types.dsl.EntityPathBase;

public record PathKey(
        EntityPathBase<?> root,
        String field,
        JoinType joinType
) {}
