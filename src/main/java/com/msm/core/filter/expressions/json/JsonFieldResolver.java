package com.msm.core.filter.expressions.json;

import com.msm.core.filter.cache.EntityMetadataFactory;
import com.msm.core.filter.domain.FieldMetadata;
import com.msm.core.filter.utils.ResolveUtils;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonFieldResolver {

    public static Expression<?> resolve(EntityPathBase<?> root, String field) {
        List<String> parts = ResolveUtils.pathAsArray(field);
        FieldMetadata metadata = EntityMetadataFactory.getFieldMetadata(root.getType(), parts);
        return JsonbExpressions.json(root, parts.getFirst(), parts.subList(1, parts.size()), metadata.getJavaType());
    }

    public static void putNested(
            Map<String,Object> root,
            String path,
            Object value) {

        String[] parts = path.split("\\.");

        Map<String,Object> current = root;

        for(int i = 0; i < parts.length - 1; i++){
            current = (Map<String,Object>) current.computeIfAbsent(
                    parts[i],
                    k -> new HashMap<>()
            );
        }

        current.put(parts[parts.length-1], value);
    }
}