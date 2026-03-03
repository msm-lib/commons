package com.msm.core.filter.join;

import com.msm.core.filter.domain.JoinKey;
import com.querydsl.core.types.dsl.PathBuilder;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ReferenceJoinCache {

    private static final Map<JoinKey, PathBuilder<?>> JOINS = new LinkedHashMap<>();

    public static PathBuilder<?> getOrCreate(JoinKey key, Supplier<PathBuilder<?>> supplier) {
        return JOINS.computeIfAbsent(key, k -> supplier.get());
    }

    public static Collection<Map.Entry<JoinKey, PathBuilder<?>>> entries() {
        return JOINS.entrySet();
    }
}

