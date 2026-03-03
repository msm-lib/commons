package com.msm.core.filter;

import com.querydsl.core.types.dsl.EntityPathBase;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"unchecked"})
public final class EntityPathResolver {

    private static final Map<Class<?>, EntityPathBase<?>> CACHE = new ConcurrentHashMap<>();

    private EntityPathResolver() {}

    public static <T> EntityPathBase<T> resolve(Class<T> entityClass) {

        return (EntityPathBase<T>) CACHE.computeIfAbsent(entityClass, EntityPathResolver::create);
    }

    private static EntityPathBase<?> create(Class<?> entityClass) {
        try {
            String qClassName = entityClass.getPackageName() + ".Q" + entityClass.getSimpleName();
            Class<?> qClass = Class.forName(qClassName);
            // find static field of same type
            for (Field f : qClass.getFields()) {
                if (Modifier.isStatic(f.getModifiers())
                        && EntityPathBase.class
                        .isAssignableFrom(f.getType())) {

                    return (EntityPathBase<?>) f.get(null);
                }
            }
            throw new IllegalStateException("No EntityPathBase field in " + qClassName);
        } catch (Exception e) {
            throw new RuntimeException("Cannot resolve Q-type for " + entityClass.getName(), e);
        }
    }
}
