package com.msm.core.validate.converter;

import java.time.LocalDate;

public class LocalDateConverter implements ValueConverter {

    public boolean supports(Class<?> t) {
        return t == LocalDate.class;
    }

    public Object convert(String raw, Class<?> t) {
        return LocalDate.parse(raw);
    }
}
