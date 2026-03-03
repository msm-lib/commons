package com.msm.core.filter.domain;

import com.querydsl.core.types.dsl.PathBuilder;

public record PathKey(
        PathBuilder<?> root,
        String field,
        JoinType joinType
) {}
