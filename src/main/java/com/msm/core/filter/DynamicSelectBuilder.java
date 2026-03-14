package com.msm.core.filter;

import com.msm.core.commons.Utils;
import com.msm.core.filter.cache.EntityMetadataRegistry;
import com.msm.core.filter.domain.FieldMetadata;
import com.msm.core.filter.join.ReferenceJoinResolver;
import com.msm.core.filter.json.JsonFieldResolver;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import java.util.*;

public final class DynamicSelectBuilder {

    private DynamicSelectBuilder() {}

    public static List<String> getOrSelectAll(List<String> fields, PathBuilder<?> root) {
        if (Utils.CL.isEmpty(fields)) {
            return new ArrayList<>(EntityMetadataRegistry.getFieldNames(root.getType()));
        }
        return fields;
    }

    public static Map<String, Expression<?>> build(List<String> fields, PathBuilder<?> root, ReferenceJoinResolver joinResolver) {
        Map<String, Expression<?>> result = new HashMap<>();
        List<String> fieldNames = getOrSelectAll(fields, root);
        for (String field : fieldNames) {
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

        FieldMetadata fieldMetadata = EntityMetadataRegistry.get(root.getType(), parts[0]);
        if(fieldMetadata.jsonType()) {
            Expression<?> expression = JsonFieldResolver.resolve(root, field);
            Expressions.as(expression, fieldMetadata.field());
            return expression;
        }

        for (int i = 0; i < parts.length - 1; i++) {
            current = joinResolver.resolve(current, parts[i]);
        }

        return current
                .get(parts[parts.length - 1])
                .as(field.replace(".", "_"));
    }
}
