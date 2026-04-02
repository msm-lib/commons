package com.msm.core.filter.sort;

import com.msm.core.filter.cache.EntityMetadataFactory;
import com.msm.core.filter.cache.PathCache;
import com.msm.core.filter.domain.FieldMetadata;
import com.msm.core.filter.domain.JoinType;
import com.msm.core.filter.domain.pageable.Sort;
import com.msm.core.filter.join.ReferenceJoinResolver;
import com.msm.core.filter.expressions.json.JsonOrderSpecifierFactory;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SortBuilder {

    public static List<OrderSpecifier<?>> build(
            List<Sort> sorts,
            PathBuilder<?> root,
            ReferenceJoinResolver resolver
    ) {
        if (sorts == null || sorts.isEmpty()) {
            return List.of();
        }

        List<OrderSpecifier<?>> result =
                new ArrayList<>();

        for (Sort s : sorts) {

            // JSONB case: data.customer.email
            if (isJsonField(s.getAttribute(), root.getType())) {

                String[] parts = s.getAttribute().split("\\.");
                List<String> jsonPath = Arrays.asList(parts).subList(1, parts.length);
                result.add(JsonOrderSpecifierFactory.build(root, parts[0], jsonPath, s.getDirection()));
                continue;
            }

            // Normal / join field
            Path<?> path = PathCache.resolve(
                    s.getAttribute(),
                    root,
                    resolver,
                    JoinType.INNER
            );

            result.add(OrderSpecifierFactory.build(path, s));
        }

        return result;
    }

    private static boolean isJsonField(String fieldPath, Class<?> rootEntity) {
        String rootField = fieldPath.contains(".")
                ? fieldPath.substring(0, fieldPath.indexOf('.'))
                : fieldPath;

        FieldMetadata f = EntityMetadataFactory.get(rootEntity, rootField);
        return f.isJsonType();
    }
}
