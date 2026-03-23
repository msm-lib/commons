package com.msm.core.commons;

import com.fasterxml.jackson.databind.JavaType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GenericTypeResolverFactory {

    private final static GenericTypeResolver INSTANCE = new GenericTypeResolver();
    private final static Map<String, JavaType> CACHE = new ConcurrentHashMap<>();
    
    public static JavaType resolve(String inputType) {
        return CACHE.computeIfAbsent(inputType, GenericTypeResolverFactory::parseInternal);
    }

    private static JavaType parseInternal(String inputType) {
        return INSTANCE.parse(inputType);
    }

    public static void registerPackage(String packageName) {
        INSTANCE.addImport(packageName);
    }

    public static void registerAlias(String simple, String fqcn) {
        INSTANCE.addAlias(simple, fqcn);
    }

    private GenericTypeResolverFactory() {}
}
