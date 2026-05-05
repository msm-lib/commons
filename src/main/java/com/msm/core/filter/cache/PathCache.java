package com.msm.core.filter.cache;

import com.msm.core.filter.domain.JoinType;
import com.msm.core.filter.domain.PathKey;
import com.msm.core.filter.join.ReferenceJoinResolver;
import com.msm.core.filter.utils.ResolveUtils;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PathCache {

    private static final Map<PathKey, Path<?>> CACHE = new ConcurrentHashMap<>();

    public static Path<?> resolve(String field, PathBuilder<?> root, ReferenceJoinResolver joinResolver, JoinType joinType) {
        PathKey key = new PathKey(root, field, joinType);
        return CACHE.computeIfAbsent(key, k -> ResolveUtils.resolvePath(field, root, joinResolver, joinType));
    }

    private PathCache() {}
}
