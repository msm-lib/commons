package com.msm.core.validate.converter;

public class NumberConverter implements ValueConverter {

    public boolean supports(Class<?> t) {
        return Number.class.isAssignableFrom(t)
                || t.isPrimitive();
    }

    public Object convert(String raw, Class<?> t) {
        if (t == Integer.class || t == int.class)
            return Integer.valueOf(raw);
        if (t == Long.class || t == long.class)
            return Long.valueOf(raw);
        if (t == Double.class || t == double.class)
            return Double.valueOf(raw);
        throw new IllegalArgumentException("Unsupported number " + t);
    }
}
