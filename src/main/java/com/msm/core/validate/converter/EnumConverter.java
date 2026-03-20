package com.msm.core.validate.converter;

public class EnumConverter implements ValueConverter {

    public boolean supports(Class<?> t) {
        return t.isEnum();
    }

    @SuppressWarnings("unchecked")
    public Object convert(String raw, Class<?> t) {
        return Enum.valueOf((Class<Enum>) t, raw);
    }
}