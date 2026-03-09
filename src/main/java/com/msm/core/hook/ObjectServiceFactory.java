package com.msm.core.hook;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public final class ObjectServiceFactory {

    private static final Map<String, Object> INSTANCES = new ConcurrentHashMap<>();

    private ObjectServiceFactory() {}

    public static <T> void register(String name, T instance) {
        INSTANCES.put(name, instance);
    }

    public static <T> void registerGroup(String groupName, String name, T instance) {
        Map<String, Object> objectMap = (Map<String, Object>) INSTANCES.computeIfAbsent(groupName, key -> new ConcurrentHashMap<>());
        objectMap.put(name, instance);
        INSTANCES.put(name, objectMap);
    }

    public static <T> T get(String name) {
        Object service = INSTANCES.get(name);
        if (Objects.isNull(service)) {
            throw new IllegalStateException("Service type mismatch for: " + name);
        }

        return (T) service;
    }

    public static <T> T get(String type, Class<T> clazz) {
        Object service = INSTANCES.get(type);

        if (Objects.isNull(service) || !clazz.isInstance(service)) {
            throw new IllegalStateException("Service type mismatch for: " + type);
        }

        return clazz.cast(service);
    }

    public static <T> Map<String, T> getGroup(String groupName) {
        Object service = INSTANCES.get(groupName);
        if (Objects.isNull(service)) {
            throw new IllegalStateException("Service type mismatch for: " + groupName);
        }

        return (Map<String, T>) service;
    }

    public static boolean contains(String name) {
        return INSTANCES.containsKey(name);
    }

}