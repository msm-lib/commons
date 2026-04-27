package com.msm.core.filter.cache;

import com.msm.core.commons.ValueConvertFactory;
import com.msm.core.filter.domain.FieldMetadata;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public final class EntityMetadataFactory {
    private static final Map<Class<?>, Map<String, FieldMetadata>> CACHE = new ConcurrentHashMap<>();

    public static FieldMetadata get(Class<?> entityClass, String fieldPath) {
        return CACHE.computeIfAbsent(entityClass, EntityMetadataFactory::scan).get(fieldPath);
    }

    public static List<FieldMetadata> getFieldMetadataList(Class<?> entityClass) {
        return new ArrayList<>(CACHE.computeIfAbsent(entityClass, EntityMetadataFactory::scan).values());
    }

    public static Set<String> getFieldNames(Class<?> entityClass) {
        return CACHE.computeIfAbsent(entityClass, EntityMetadataFactory::scan).keySet();
    }

    private static Map<String, FieldMetadata> scan(Class<?> entityClass) {
        Map<String, FieldMetadata> map = new HashMap<>();
        Set<String> objectTraversal = new HashSet<>();
        scanRecursive(entityClass, "", map, objectTraversal);
        return map;
    }

    private static void scanRecursive(Class<?> type, String prefix, Map<String, FieldMetadata> map, Set<String> objectTraversal) {
        Class<?> current = type;
        if (objectTraversal.contains(current.getName())) {
            return;
        }
        objectTraversal.add(current.getName());
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                Class<?> fieldType = field.getType();
                if(isSerial(field)) continue;
                String fullPath = prefix.isEmpty() ? field.getName() : prefix + "." + field.getName();
                boolean jsonType = isJsonField(field);
                boolean isRelation = isRelation(field);
                map.put(fullPath, new FieldMetadata(
                        fullPath,
                        fieldType,
                        Comparable.class.isAssignableFrom(ValueConvertFactory.normalizeDataType(fieldType)),
                        Objects.equals(fieldType, String.class),
                        fieldType.isEnum(),
                        jsonType,
                        fieldType.isAnnotationPresent(Embeddable.class),
                        isRelation)
                );

                // Entity relation → recurse
//                if (fieldType.isAnnotationPresent(Entity.class)) {
//                    scanRecursive(fieldType, fullPath, map, objectTraversal);
//                }

//                if (isRelation) {
//                    scanRecursive(fieldType, fullPath, map);
//                }

                if (fieldType.isAnnotationPresent(Embeddable.class)) {
                    scanRecursive(fieldType, fullPath, map, objectTraversal);
                }
            }

            current = current.getSuperclass();
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

    private static boolean isRelation(Field f) {
        return f.isAnnotationPresent(ManyToOne.class)
                || f.isAnnotationPresent(OneToMany.class)
                || f.isAnnotationPresent(OneToOne.class);
    }

    private static boolean isSerial(Field f) {
        return f.getName().equals("serialVersionUID");
    }

    public static FieldMetadata getFieldMetadata(Class<?> clazz, List<String> parts) {

        FieldMetadata fieldMetadata = get(clazz, parts.getFirst());
        if(parts.size() == 1) {
            return fieldMetadata;
        }
        Class<?> entityClass = fieldMetadata.getJavaType();
        for (int i = 1; i <= parts.size() - 1; i++) {
            FieldMetadata meta = get(entityClass, parts.get(i));
            if(Objects.nonNull(meta)) {
                fieldMetadata = meta;
                entityClass = meta.getJavaType();
            }
        }
        return fieldMetadata;
    }
}

