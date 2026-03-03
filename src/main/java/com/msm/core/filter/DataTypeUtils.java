package com.msm.core.filter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public final class DataTypeUtils {
    private DataTypeUtils() {}
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS = Map.of(
            int.class, Integer.class,
            long.class, Long.class,
            double.class, Double.class,
            boolean.class, Boolean.class,
            float.class, Float.class,
            short.class, Short.class,
            byte.class, Byte.class,
            char.class, Character.class
    );
    private static final Map<Class<?>, Function<String, ?>> CONVERTERS = Map.of(
            Integer.class, Integer::valueOf,
            Long.class, Long::valueOf,
            Double.class, Double::valueOf,
            BigDecimal.class, BigDecimal::new,
            Boolean.class, Boolean::valueOf,
            LocalDate.class, LocalDate::parse,
            LocalDateTime.class, LocalDateTime::parse,
            UUID.class, UUID::fromString
    );

    public static Class<?> normalize(Class<?> type) {
        return type.isPrimitive() ? DataTypeUtils.PRIMITIVE_WRAPPERS.get(type) : type;
    }

    public static Function<String, ?> getCastFunction(Class<?> targetType) {
        Class<?> normalized = normalize(targetType);
        return CONVERTERS.get(normalized);
    }
}
