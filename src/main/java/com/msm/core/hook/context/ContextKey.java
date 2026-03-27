package com.msm.core.hook.context;

import lombok.Data;

@Data
public class ContextKey<T> {
    private final String name;

    private ContextKey(String name) {
        this.name = name;
    }

    public static <T> ContextKey<T> of(String name) {
        return new ContextKey<>(name);
    }

}