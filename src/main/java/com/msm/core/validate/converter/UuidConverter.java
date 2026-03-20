package com.msm.core.validate.converter;

import java.util.UUID;

public class UuidConverter implements ValueConverter {

    public boolean supports(Class<?> t) {
        return t == UUID.class;
    }

    public Object convert(String raw, Class<?> t) {
        return UUID.fromString(raw);
    }
}