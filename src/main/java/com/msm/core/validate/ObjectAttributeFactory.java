package com.msm.core.validate;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
@Slf4j
public class ObjectAttributeFactory {

    private static final Map<String, Object> INSTANCES = new ConcurrentHashMap<>();

    private ObjectAttributeFactory() {}

    public static <T> void register(String compositeKey, T instance) {
        Object existing = INSTANCES.putIfAbsent(compositeKey, instance);
        if (Objects.isNull(existing)) {
            log.warn("Key {} already has a registered instance. Skipping overwrite.", compositeKey);
        }
    }

    public static <T> Optional<T> get(String compositeKey) {
        Object value = INSTANCES.get(compositeKey);

        if (Objects.isNull(value)) {
            log.warn("No instance found for key: {}", compositeKey);
            return Optional.empty();
        }
        return Optional.of((T) value);
    }

    public static <T> Optional<T> get(String compositeKey, Class<T> type) {
        Object value = INSTANCES.get(compositeKey);

        if (Objects.isNull(value)) {
            log.warn("No instance found for key: {}", compositeKey);
            return Optional.empty();
        }

        if (!type.isInstance(value)) {
            log.error("Type mismatch for key {}. Expected: {}, Found: {}", compositeKey, type.getName(), value.getClass().getName());
            return Optional.empty();
        }

        return Optional.of(type.cast(value));
    }

    public static boolean contains(String compositeKey) {
        return INSTANCES.containsKey(compositeKey);
    }
}
