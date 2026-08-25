package com.msm.core.metadata.typesafe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class DataRecord {

    private final Map<String, Object> values;

    private DataRecord(Map<String, Object> values) {
        this.values = values;
    }

    public <T> T get(TypedAttribute<T> field) {
        return field.resolve(this);
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

    public <T> T getOrDefault(TypedAttribute<T> field, T defaultValue) {
        return Optional.ofNullable(get(field)).orElse(defaultValue);
    }

    public <T> void set(TypedAttribute<T> field, T value) {
        if (field.isComputed()) {
            throw new IllegalArgumentException("Cannot set computed attribute: " + field.getFieldName());
        }
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

    public boolean isEmpty() {
        return Utils.CL.isEmpty(values);
    }

    public boolean isNotEmpty() {
        return !isEmpty();
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

    private String getRefName(TypedAttribute<?> field) {
        return Utils.STR.format(Constants.ATTRIBUTE_REF_TEMPLATE, field.getFieldName());
    }


    // helper method
    public <T> T get(String name, Class<T> clazz) {
        return Utils.O.convertToType(values.get(name), clazz);
    }

    public <T> T get(String name, TypeReference<T> clazz) {
        return Utils.O.convertToType(values.get(name), clazz);
    }

    public <T> Optional<T> getOptional(String name, Class<T> clazz) {
        return Optional.ofNullable(get(name, clazz));
    }

    public <T> T getOrDefault(String name, Class<T> clazz, T defaultValue) {
        return Optional.ofNullable(get(name, clazz)).orElse(defaultValue);
    }

    public <T> Optional<T> getOptional(String name, TypeReference<T> clazz) {
        return Optional.ofNullable(get(name, clazz));
    }

    public <T> T getOrDefault(String name, TypeReference<T> clazz, T defaultValue) {
        return Optional.ofNullable(get(name, clazz)).orElse(defaultValue);
    }

    public boolean contains(String name) {
        return values.containsKey(name);
    }

    public void remove(String name) {
        values.remove(name);
    }



    //static helper method
    public static DataRecord of(Map<String, Object> map) {
        return new DataRecord(map);
    }

    public static DataRecord ofNullable(Map<String, Object> map) {
        return new DataRecord(Utils.CL.emptyIfNull(map));
    }

    public static DataRecord of() {
        return new DataRecord(new HashMap<>());
    }

    public static List<DataRecord> of(List<Map<String, Object>> maps) {
        return Utils.CL.emptyIfNull(maps).stream().map(DataRecord::ofNullable).collect(Collectors.toList());
    }

    public static List<Map<String, Object>> toMapList(List<DataRecord> dataRecords) {
        return Utils.D.toMapList(dataRecords, DataRecord::getValues);
    }

}