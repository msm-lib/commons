package com.msm.core.validate.converter;

import java.util.List;

public class ConverterRegistry {

    private final List<ValueConverter> converters = List.of(
            new NumberConverter(),
            new EnumConverter(),
            new LocalDateConverter(),
            new LocalDateTimeConverter(),
            new UuidConverter(),
            new StringConverter()
    );

    public Object convert(String raw, Class<?> targetType) {
        return converters.stream()
                .filter(c -> c.supports(targetType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No converter for " + targetType))
                .convert(raw, targetType);
    }
}
