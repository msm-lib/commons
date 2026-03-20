package com.msm.core.filter.domain;

import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;

public record PathKey(
        EntityPathBase<?> root,
        String field,
        JoinType joinType
) {}
