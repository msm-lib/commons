package com.msm.core.metadata.annotation;

public @interface Property {
    Name name();
    String value();


    enum Name {
        REQUIRED,
        FREE_TEXT,
        DEFAULT,
        MIN_VALUE,
        MAX_VALUE,
        MIN_LENGTH,
        MAX_LENGTH,
        REGEX
    }
}