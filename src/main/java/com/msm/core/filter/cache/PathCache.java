package com.msm.core.filter.cache;

import com.msm.core.filter.domain.JoinType;
import com.msm.core.filter.domain.PathKey;
import com.msm.core.filter.join.ReferenceJoinResolver;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PathCache {

    private static final Map<PathKey, Path<?>> CACHE = new ConcurrentHashMap<>();

    public static Path<?> resolve(String field, PathBuilder<?> root, ReferenceJoinResolver joinResolver, JoinType joinType) {
        PathKey key = new PathKey(root, field, joinType);
        return CACHE.computeIfAbsent(key, k -> resolveInternal(field, root, joinResolver, joinType));
    }

//    public static Path<?> resolve(String field, PathBuilder<?> root) {
//        PathKey key = new PathKey(root, field, null);
//        return CACHE.computeIfAbsent(key, k -> resolveInternal(field, root, null, null));
//    }

    private static Path<?> resolveInternal(String field, PathBuilder<?> root, ReferenceJoinResolver joinResolver, JoinType joinType) {
        if (!field.contains(".")) {
            return root.get(field);
        }

        PathBuilder<?> current = root;
        String[] parts = field.split("\\.");

        for (int i = 0; i < parts.length - 1; i++) {
            current = joinResolver.resolve(current, parts[i]);
        }

        return current.get(parts[parts.length - 1]);
    }

    private PathCache() {}
}
