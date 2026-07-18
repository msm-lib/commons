package com.msm.core.metadata.typesafe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;

import java.util.Collections;
import java.util.HashMap;
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

    public <X> X getRef(TypedAttribute<?> field, Class<X> type) {
        return Utils.O.convertToType(values.get(getRefName(field)), type);
    }

    public <X> X getRef(TypedAttribute<?> field, TypeReference<X> typeReference) {
        return Utils.O.convertToType(values.get(getRefName(field)), typeReference);
    }

    public Map<String, Object> getRefAsMap(TypedAttribute<?> field) {
        return Utils.O.convertToMap(values.get(getRefName(field)), String.class, Object.class);
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
    public static DataRecord of() {
        return new DataRecord(new HashMap<>());
    }

    private String getRefName(TypedAttribute<?> field) {
        return Utils.STR.format(Constants.ATTRIBUTE_REF_TEMPLATE, field.getFieldName());
    }


    //static helper method
    public <T> T get(String name, Class<T> clazz) {
        return Utils.O.convertToType(values.get(name), clazz);
    }

    public <T> T get(String name, TypeReference<T> clazz) {
        return Utils.O.convertToType(values.get(name), clazz);
    }

    public <T> Optional<T> getOptional(String name, Class<T> clazz) {
        return Optional.ofNullable(get(name, clazz));
    }

    public <T> Optional<T> getOptional(String name, TypeReference<T> clazz) {
        return Optional.ofNullable(get(name, clazz));
    }

    public boolean contains(String name) {
        return values.containsKey(name);
    }

    public void remove(String name) {
        values.remove(name);
    }



}