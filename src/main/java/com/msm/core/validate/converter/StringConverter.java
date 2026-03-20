package com.msm.core.validate.converter;

public class StringConverter implements ValueConverter {

    public boolean supports(Class<?> t) {
        return t == String.class;
    }

    public Object convert(String raw, Class<?> t) {
        return raw;
    }
}