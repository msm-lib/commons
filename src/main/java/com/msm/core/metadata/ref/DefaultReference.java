package com.msm.core.metadata.ref;


import java.util.List;
import java.util.Map;

public enum DefaultReference implements RefDataDefinition {
    DEFAULT_REFERENCE(
            Map.class,
            "id",
            "code",
            "name"
    );


    private Class<?> targetType;
    private final List<String> fields;

    DefaultReference(String... fields) {
        this.fields = List.of(fields);
    }

    DefaultReference(Class<?> targetType, String... fields) {
        this.targetType = targetType;
        this.fields = List.of(fields);
    }

    @Override
    public List<String> fields() {
        return fields;
    }

    @Override
    public Class<?> targetType() {
        return targetType;
    }
}