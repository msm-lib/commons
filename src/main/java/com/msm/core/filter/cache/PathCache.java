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

//    public static Path<?> resolve(String field, PathBuilder<?> root) {
//        PathKey key = new PathKey(root, field, null);
//        return CACHE.computeIfAbsent(key, k -> resolveInternal(field, root, null, null));
//    }

//    private static Path<?> resolveInternal(String field, PathBuilder<?> root, ReferenceJoinResolver joinResolver, JoinType joinType) {
//        if (!field.contains(".")) {
//            return root.get(field);
//        }
//
//        String[] parts = field.split("\\.");
//
//        PathBuilder<?> current = root;
//
//
////        for (int i = 0; i < parts.length - 1; i++) {
////            current = joinResolver.resolve(current, parts[i]);
////        }
//
////        FieldMetadata fieldMetadata = EntityMetadataFactory.get(root.getType(), parts[0]);
////        if(fieldMetadata.jsonType()) {
//////            Expression<?> expression = JsonFieldResolver.resolve(root, field);
//////            Expressions.as(expression, fieldMetadata.field());
//////            StringExpression stringExpression = (StringExpression) expression;
////
////            return root.get(parts[0]);
////        }
//
//        Class<?> currentType = root.getType();
//        for (int i = 0; i < parts.length - 1; i++) {
//            String part = parts[i];
//            FieldMetadata fieldMetadata = EntityMetadataFactory.get(currentType, part);
//            //For json
//            if(fieldMetadata.isJsonType()) {
//                return current.get(part);
//            }
//
//            //For embedded
//            if (fieldMetadata.isEmbedded()) {
//                current = current.get(part);
//                currentType = fieldMetadata.getJavaType();
//                continue;
//            }
//
//            // COLUMN (last node)
//            if (i == parts.length - 1) {
//                return current.get(part);
//            }
//
//            // invalid traversal
//            throw new RuntimeException(
//                    "Invalid nested field: " + field
//            );
//        }
//
//        return current.get(parts[parts.length - 1]);
//    }
//
//
//public static Expression<?> resolveInternal(
//        String field,
//        PathBuilder<?> root,
//        Class<?> rootEntity,
//
//
//        ReferenceJoinResolver joinResolver) {
//
//    String[] parts = field.split("\\.");
//
//    PathBuilder<?> current = root;
//    Class<?> currentType = rootEntity;
//
//    StringBuilder pathKey = new StringBuilder();
//
//    for (int i = 0; i < parts.length; i++) {
//
//        String part = parts[i];
//
//        FieldMetadata fieldMetadata = EntityMetadataFactory.get(currentType, part);
//
//
//        if (fieldMetadata == null) {
//            throw new RuntimeException("Invalid field: " + field);
//        }
//
//        // JSON → delegate
//        if (fieldMetadata.isJsonType()) {
//            return JsonbExpressions.json(
//                    current,
//                    field,
//                    String.class
//            );
//        }
//
//        // RELATION → join reuse
////        if (fieldMetadata.isRelation()) {
////
////            if (pathKey.length() > 0) pathKey.append(".");
////            pathKey.append(part);
////
////            current = (PathBuilder<?>) joinManager.getOrCreate(
////                    pathKey.toString(),
////                    current,
////                    part
////            );
////
////            currentType = meta.getJavaType();
////            continue;
////        }
//
//        // EMBEDDED
//        if (fieldMetadata.isEmbedded()) {
//            current = current.get(part);
//            currentType = fieldMetadata.getJavaType();
//            continue;
//        }
//
//        // COLUMN (last node)
//        if (i == parts.length - 1) {
//            return current.get(part);
//        }
//
//        // invalid traversal
//        throw new RuntimeException(
//                "Invalid nested field: " + field
//        );
//    }
//
//    throw new RuntimeException("Cannot resolve: " + field);
//}

//    public static List<String> pathAsArray(String field) {
//        return Arrays.stream(field.split("\\.")).collect(Collectors.toList());
//    }

    private PathCache() {}
}
