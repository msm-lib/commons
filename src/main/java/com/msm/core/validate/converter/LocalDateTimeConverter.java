package com.msm.core.validate.converter;


import java.time.LocalDateTime;

public class LocalDateTimeConverter implements ValueConverter {

    public boolean supports(Class<?> t) {
        return t == LocalDateTime.class;
    }

    public Object convert(String raw, Class<?> t) {
        return LocalDateTime.parse(raw);
    }
}
