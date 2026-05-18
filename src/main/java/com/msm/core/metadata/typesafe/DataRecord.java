package com.msm.core.metadata.typesafe;

import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public final class DataRecord {

    private final Map<String, Object> values;

    private DataRecord(Map<String, Object> values) {
        this.values = values;
    }

    public <T> T get(TypedAttribute<T> field) {
        return field.cast(values.get(field.getFieldName()));
    }
    public Object getRef(TypedAttribute<?> field) {
        return values.get(getRefName(field));
    }

    public <T> Optional<T> getOptional(TypedAttribute<T> field) {
        return Optional.ofNullable(get(field));
    }

    public <T> void set(TypedAttribute<T> field, T value) {
        values.put(field.getFieldName(), value);
    }

    public void setRef(TypedAttribute<?> field, Object value) {
        values.put(getRefName(field), value);
    }

    public void put(String fieldName, Object value) {
        values.put(fieldName, value);
    }

    public void putIfAbsent(String fieldName, Object value) {
        values.putIfAbsent(fieldName, value);
    }

    public <T> void putIfAbsent(TypedAttribute<T> field, T value) {
        values.putIfAbsent(field.getFieldName(), value);
    }

    public boolean contains(TypedAttribute<?> field) {
        return values.containsKey(field.getFieldName());
    }

    public void remove(TypedAttribute<?> field) {
        values.remove(field.getFieldName());
    }

    public Map<String, Object> toMap() {
        return Collections.unmodifiableMap(values);
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public static DataRecord of(Map<String, Object> map) {
        return new DataRecord(map);
    }

    private String getRefName(TypedAttribute<?> field) {
        return Utils.STR.format(Constants.ATTRIBUTE_REF_TEMPLATE, field.getFieldName());
    }
}