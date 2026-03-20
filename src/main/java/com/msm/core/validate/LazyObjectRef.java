package com.msm.core.validate;

import java.util.function.Supplier;

public class LazyObjectRef {
    private final Supplier<Object> loader;
    private Object value;

    public LazyObjectRef(Supplier<Object> loader) {
        this.loader = loader;
    }

    public Object get() {
        if (value == null) {
            value = loader.get();
        }
        return value;
    }
}
