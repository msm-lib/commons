package com.msm.core.filter;

import com.msm.core.filter.join.ReferenceJoinResolver;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.PathBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DynamicSelectBuilder {

    private DynamicSelectBuilder() {
    }

    public static Map<String, Expression<?>> build(List<String> fields, PathBuilder<?> root, ReferenceJoinResolver joinResolver) {
        Map<String, Expression<?>> result = new HashMap<>();

        for (String field : fields) {
            result.computeIfAbsent(field, f -> resolveExpression(f, root, joinResolver));
        }

        return result;
    }

    private static Expression<?> resolveExpression(String field, PathBuilder<?> root, ReferenceJoinResolver joinResolver) {
        if (!field.contains(".")) {
            return root.get(field).as(field);
        }

        String[] parts = field.split("\\.");
        PathBuilder<?> current = root;

        for (int i = 0; i < parts.length - 1; i++) {
            current = joinResolver.resolve(current, parts[i]);
        }

        return current
                .get(parts[parts.length - 1])
                .as(field.replace(".", "_"));
    }
}
