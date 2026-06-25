package com.msm.core.metadata.ref;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ReflectionCache {

    private ReflectionCache() {
    }

    private static final Map<Class<?>, Map<String, Field>> FIELD_CACHE =
            new ConcurrentHashMap<>();

    private static final Map<Field, Class<?>> LIST_ELEMENT_CACHE =
            new ConcurrentHashMap<>();

    public static Field findField(Class<?> type, String fieldName) {

        return FIELD_CACHE
                .computeIfAbsent(type, ReflectionCache::scanFields)
                .get(fieldName);
    }

    private static Map<String, Field> scanFields(Class<?> type) {

        Map<String, Field> result = new ConcurrentHashMap<>();

        Class<?> current = type;

        while (current != null && current != Object.class) {

            for (Field field : current.getDeclaredFields()) {
                field.setAccessible(true);
                result.putIfAbsent(field.getName(), field);
            }

            current = current.getSuperclass();
        }

        return result;
    }

    public static Class<?> getListElementType(Field field) {

        return LIST_ELEMENT_CACHE.computeIfAbsent(field, f -> {

            Type genericType = f.getGenericType();

            if (!(genericType instanceof ParameterizedType pt)) {
                return Object.class;
            }

            Type actual = pt.getActualTypeArguments()[0];

            if (actual instanceof Class<?> clazz) {
                return clazz;
            }

            return Object.class;
        });
    }
}
