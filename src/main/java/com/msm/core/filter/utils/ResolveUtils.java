package com.msm.core.filter.utils;

import com.msm.core.exceptions.CommonErrors;
import com.msm.core.filter.cache.EntityMetadataFactory;
import com.msm.core.filter.domain.FieldMetadata;
import com.msm.core.filter.domain.JoinType;
import com.msm.core.filter.expressions.json.JsonFieldResolver;
import com.msm.core.filter.join.ReferenceJoinResolver;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResolveUtils {

    public static Path<?> resolvePath(String field, PathBuilder<?> root, ReferenceJoinResolver joinResolver, JoinType joinType) {
        if (!field.contains(".")) {
            return root.get(field);
        }

        String[] parts = field.split("\\.");
        PathBuilder<?> current = root;
        Class<?> currentType = root.getType();
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            FieldMetadata fieldMetadata = EntityMetadataFactory.get(currentType, part);
            if (Objects.isNull(fieldMetadata)) {
                throw CommonErrors.invalid(field, "Invalid field: " + field);
            }
            //For json
            if(fieldMetadata.isJsonType()) {
                return current.get(part);
            }

            //For embedded
            if (fieldMetadata.isEmbedded()) {
                current = current.get(part);
                currentType = fieldMetadata.getJavaType();
                continue;
            }

            // COLUMN (last node)
            if (i == parts.length - 1) {
                return current.get(part);
            }

            // invalid traversal
            throw CommonErrors.invalid(field, "Invalid nested field: " + field);
        }

        return current.get(parts[parts.length - 1]);
    }


    public static Expression<?> resolveExpression(String field, PathBuilder<?> root, ReferenceJoinResolver joinResolver) {

        String[] parts = field.split("\\.");
        PathBuilder<?> current = root;
        Class<?> currentType = root.getType();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            FieldMetadata fieldMetadata = EntityMetadataFactory.get(currentType, part);
            if (fieldMetadata == null) {
                throw CommonErrors.invalid(field, "Invalid field: " + field);
            }

            // COLUMN (last node)
            if (i == parts.length - 1) {
                return current.get(part);
            }

            // JSON → delegate
            if (fieldMetadata.isJsonType()) {
                Expression<?> expression = JsonFieldResolver.resolve(root, field);
                Expressions.as(expression, fieldMetadata.getField());
                return expression;
            }

            // EMBEDDED
            if (fieldMetadata.isEmbedded()) {
                current = current.get(part);
                currentType = fieldMetadata.getJavaType();
                continue;
            }

            // invalid traversal
            throw CommonErrors.invalid(field, "Invalid nested field: " + field);
        }

        throw new RuntimeException("Cannot resolve: " + field);
    }

    public static List<String> extractPath(Path<?> path) {
        List<String> parts = new LinkedList<>();
        Path<?> current = path;
        while (current != null) {
            String strPath = current.getMetadata().getName();
            parts.addFirst(strPath);
            current = current.getMetadata().getParent();
        }
        parts.removeFirst();
        return parts;
    }

    public static String resolveStringPath(Path<?> path) {
        return String.join(".", ResolveUtils.extractPath(path));
    }

    public static List<String> pathAsArray(String field) {
        return Arrays.stream(field.split("\\.")).collect(Collectors.toList());
    }
}
