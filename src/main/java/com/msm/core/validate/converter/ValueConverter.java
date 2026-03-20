package com.msm.core.validate.converter;

public interface ValueConverter {
    boolean supports(Class<?> targetType);
    Object convert(String raw, Class<?> targetType);
}
