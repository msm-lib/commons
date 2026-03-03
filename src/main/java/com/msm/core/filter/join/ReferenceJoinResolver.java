package com.msm.core.filter.join;

import com.msm.core.filter.domain.JoinKey;
import com.msm.core.filter.domain.ReferenceJoinMetadata;
import com.querydsl.core.types.dsl.PathBuilder;

public class ReferenceJoinResolver {

    public ReferenceJoinResolver() {}

    public PathBuilder<?> resolve(PathBuilder<?> from, String refName) {
        ReferenceJoinMetadata ref = ReferenceJoinMetadataRegistry.get(from.getType(), refName);
        if (ref == null) {
            throw new IllegalArgumentException("Unknown reference '" + refName + "' on " + from.getType().getSimpleName());
        }

        JoinKey key = new JoinKey(from.getType(), refName);
        return ReferenceJoinCache.getOrCreate(
                key,
                () -> new PathBuilder<>(
                        ref.targetEntity(),
                        from.getMetadata().getName() + "_" + refName
                )
        );
    }
}

