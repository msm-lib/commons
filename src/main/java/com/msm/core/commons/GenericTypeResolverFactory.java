package com.msm.core.commons;

import com.fasterxml.jackson.databind.JavaType;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GenericTypeResolverFactory {
    private static final JavaType[] EMPTY_TYPES = new JavaType[0];
    private final static GenericTypeResolver INSTANCE = new GenericTypeResolver();
    private final static Map<String, JavaType> CACHE = new ConcurrentHashMap<>();
    private static final Map<TypeKey, JavaType> TYPE_CACHE = new ConcurrentHashMap<>();
    
    public static JavaType resolve(String inputType) {
        return CACHE.computeIfAbsent(inputType, GenericTypeResolverFactory::parseInternal);
    }

    public static JavaType resolve(Class<?> rawClass) {
        return TYPE_CACHE.computeIfAbsent(new TypeKey(rawClass, EMPTY_TYPES), k -> INSTANCE.construct(rawClass));
    }

    public static JavaType resolve(String rawType, String... params) {
        Class<?> rawClass = INSTANCE.resolveClass(rawType);
        JavaType[] javaTypes = new JavaType[params.length];
        for (int i = 0; i < params.length; i++) {
            javaTypes[i] = resolve(params[i]);
        }

        return resolve(rawClass, javaTypes);
    }

    public static JavaType resolve(Class<?> rawClass, JavaType... params) {
        return TYPE_CACHE.computeIfAbsent(TypeKey.of(rawClass, params), k -> INSTANCE.construct(rawClass, params));
    }

    public static JavaType resolve(Class<?> rawClass, Class<?>... params) {
        JavaType[] javaTypes = Arrays.stream(params)
                .map(INSTANCE::constructType)
                .toArray(JavaType[]::new);

        return resolve(rawClass, javaTypes);
    }

    private static JavaType parseInternal(String inputType) {
        return INSTANCE.parse(inputType);
    }

    public static void registerPackage(String packageName) {
        INSTANCE.addImport(packageName);
    }

    public static void registerAlias(String simple, String fullyQualifiedClassName) {
        INSTANCE.addAlias(simple, fullyQualifiedClassName);
    }

    private GenericTypeResolverFactory() {}
}
