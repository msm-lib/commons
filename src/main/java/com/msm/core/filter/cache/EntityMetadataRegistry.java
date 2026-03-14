package com.msm.core.filter.cache;

import com.msm.core.commons.Utils;
import com.msm.core.filter.domain.FieldMetadata;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class EntityMetadataRegistry {
    private static final Map<Class<?>, Map<String, FieldMetadata>> CACHE = new ConcurrentHashMap<>();

    public static FieldMetadata get(Class<?> entityClass, String fieldPath) {
        return CACHE.computeIfAbsent(entityClass, EntityMetadataRegistry::scan).get(fieldPath);
    }

    public static List<FieldMetadata> getFieldMetadataList(Class<?> entityClass) {
        return new ArrayList<>(CACHE.computeIfAbsent(entityClass, EntityMetadataRegistry::scan).values());
    }

    public static Set<String> getFieldNames(Class<?> entityClass) {
        return CACHE.computeIfAbsent(entityClass, EntityMetadataRegistry::scan).keySet();
    }

    private static Map<String, FieldMetadata> scan(Class<?> entityClass) {
        Map<String, FieldMetadata> map = new HashMap<>();
        scanRecursive(entityClass, "", map);

        return map;
    }

    private static void scanRecursive(Class<?> type, String prefix, Map<String, FieldMetadata> map) {
        for (Field field : type.getDeclaredFields()) {
            Class<?> fieldType = field.getType();
            String fullPath = prefix.isEmpty() ? field.getName() : prefix + "." + field.getName();
            boolean jsonType = isJsonField(field);
            map.put(fullPath, new FieldMetadata(
                    fullPath,
                    fieldType,
                    Comparable.class.isAssignableFrom(Utils.O.normalize(fieldType)),
                    Objects.equals(fieldType, String.class),
                    fieldType.isEnum(),
                    jsonType)
            );

            // Entity relation → recurse
            if (fieldType.isAnnotationPresent(Entity.class)) {
                scanRecursive(fieldType, fullPath, map);
            }
        }
    }

    private static boolean isJsonField(Field f) {
        if (f == null) return false;
        Column col = f.getAnnotation(Column.class);
        if (col != null
                && col.columnDefinition() != null
                && (col.columnDefinition().toLowerCase().contains("json") || col.columnDefinition().toLowerCase().contains("jsonb"))) {
            return true;
        }

        JdbcTypeCode type = f.getAnnotation(JdbcTypeCode.class);
        if (type != null && type.value() == SqlTypes.JSON) {
            return true;
        }

        return Map.class.isAssignableFrom(f.getType())
                || f.getType().getName().contains("Json")
                || f.getType().getName().contains("PGobject");
    }
}

