package com.msm.core.validate;

import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
@Slf4j
public class ObjectAttributeFactory {

    private static final Map<String, Object> INSTANCES = new ConcurrentHashMap<>();

    private ObjectAttributeFactory() {}

    public static <T> void register(String compositeKey, T instance) {
        INSTANCES.putIfAbsent(compositeKey, instance);
    }

    public static <T> T get(String compositeKey) {
        Object service = INSTANCES.get(compositeKey);
        if (Objects.isNull(service)) {
            log.warn("Object type mismatch for key: {}", compositeKey);
            return null;
        }

        return (T) service;
    }

    public static boolean contains(String compositeKey) {
        return INSTANCES.containsKey(compositeKey);
    }
}
