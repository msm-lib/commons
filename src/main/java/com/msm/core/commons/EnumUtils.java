package com.msm.core.commons;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class EnumUtils {

    private static final Map<Class<?>, Map<String, Enum<?>>> ENUM_CACHE = new ConcurrentHashMap<>();

    EnumUtils() {
    }

    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> Optional<T> fromString(Class<T> enumClass, String value) {
        if (enumClass == null || value == null || value.trim().isEmpty()) {
            return Optional.empty();
        }

        Map<String, Enum<?>> classMap = ENUM_CACHE.computeIfAbsent(enumClass, clazz ->
                Arrays.stream(clazz.getEnumConstants())
                        .map(e -> (Enum<?>) e)
                        .collect(Collectors.toMap(
                                e -> e.name().toUpperCase(),
                                Function.identity(),
                                (existing, replacement) -> existing,
                                ConcurrentHashMap::new
                        ))
        );

        Enum<?> enumConstant = classMap.get(value.trim().toUpperCase());
        return Optional.ofNullable((T) enumConstant);
    }

    public <T extends Enum<T>> T fromStringOrDefault(Class<T> enumClass, String value, T defaultValue) {
        return fromString(enumClass, value).orElse(defaultValue);
    }

    public <T extends Enum<T>> boolean isValid(Class<T> enumClass, String value) {
        return fromString(enumClass, value).isPresent();
    }

    public void clearCache() {
        ENUM_CACHE.clear();
    }
}

