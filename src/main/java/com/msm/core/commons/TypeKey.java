package com.msm.core.commons;

import com.fasterxml.jackson.databind.JavaType;

import java.util.Arrays;

public record TypeKey(Class<?> raw, JavaType[] params) {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof TypeKey(Class<?> raw1, JavaType[] params1))) {
            return false;
        }

        return raw == raw1 && Arrays.equals(params, params1);
    }

    @Override
    public int hashCode() {
        return 31 * System.identityHashCode(raw)
                + Arrays.hashCode(params);
    }

    public static TypeKey of(Class<?> raw, JavaType... params) {
        return new TypeKey(raw, params);
    }
}