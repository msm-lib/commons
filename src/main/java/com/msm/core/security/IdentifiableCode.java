package com.msm.core.security;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface IdentifiableCode {
    String getCode();

    Map<Class<?>, Map<String, ?>> CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    static <E extends Enum<E> & IdentifiableCode> E fromCode(
            Class<E> enumType,
            String code) {

        Map<String, E> mapping = (Map<String, E>) CACHE.computeIfAbsent(
                enumType,
                clazz -> Arrays
                        .stream(enumType.getEnumConstants())
                        .collect(Collectors.toUnmodifiableMap(IdentifiableCode::getCode, Function.identity()))
        );

        return mapping.get(code);
    }
}