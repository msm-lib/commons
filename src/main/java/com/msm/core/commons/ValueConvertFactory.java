package com.msm.core.commons;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class ValueConvertFactory {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS = new ConcurrentHashMap<>(
            Map.of(
                    int.class, Integer.class,
                    long.class, Long.class,
                    double.class, Double.class,
                    boolean.class, Boolean.class,
                    float.class, Float.class,
                    short.class, Short.class,
                    byte.class, Byte.class,
                    char.class, Character.class
            )
    );

    private static final Map<Class<?>, Function<String, ?>> CONVERTERS = new ConcurrentHashMap<>(
        Map.of(Integer.class, Integer::valueOf,
                Long.class, Long::valueOf,
                Double.class, Double::valueOf,
                BigDecimal.class, BigDecimal::new,
                Boolean.class, Boolean::valueOf,
                LocalDate.class, LocalDate::parse,
                LocalDateTime.class, LocalDateTime::parse,
                UUID.class, UUID::fromString)
    );

    public static void registerPrimitiveType(Class<?> from, Class<?> to) {
        PRIMITIVE_WRAPPERS.putIfAbsent(from, to);
    }

    public static Class<?> normalizeDataType(Class<?> type) {
        return type.isPrimitive() ? PRIMITIVE_WRAPPERS.get(type) : type;
    }

    public static Function<String, ?> getCastFunction(Class<?> targetType) {
        Class<?> normalized = normalizeDataType(targetType);
        return CONVERTERS.get(normalized);
    }

    public static void registerConverter(Class<?> type, Function<String, ?> attributeTypeHandler) {
        CONVERTERS.putIfAbsent(type, attributeTypeHandler);
    }

    public static <T> T convert(Class<?> targetType, Object value) {
        if (Objects.isNull(value)) return null;
        if (targetType.isInstance(value)) {
            return (T) value;
        }
        if (targetType.isEnum()) {
            return (T) Enum.valueOf((Class<? extends Enum>) targetType, value.toString());
        }
        Function<String, ?> fn = getCastFunction(targetType);
        if (Objects.nonNull(fn)) {
            return (T) fn.apply(value.toString());
        }

        throw new IllegalArgumentException("Cannot cast value '" + value + "' to type " + targetType.getName());
    }
}
