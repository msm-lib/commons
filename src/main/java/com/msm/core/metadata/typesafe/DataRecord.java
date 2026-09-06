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

    public <T> Optional<T> getOptional(TypedAttribute<T> field) {
        return Optional.ofNullable(get(field));
    }

    public <T> T getOrDefault(TypedAttribute<T> field, T defaultValue) {
        return Optional.ofNullable(get(field)).orElse(defaultValue);
    }

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

    public Optional<Object> getRefOptional(TypedAttribute<?> field) {
        return Optional.ofNullable(getRef(field));
    }

    public <X> Optional<X> getRefOptional(TypedAttribute<?> field, Class<X> type) {
        return Optional.ofNullable(getRef(field, type));
    }

    public <X> Optional<X> getRefOptional(TypedAttribute<?> field, TypeReference<X> typeReference) {
        return Optional.ofNullable(getRef(field, typeReference));
    }

    public DataRecord with(String name, Object value) {
        values.put(name, value);
        return this;
    }

    public <T> DataRecord with(TypedAttribute<T> field, T value) {
        set(field, value);
        return this;
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

    public boolean contains(String name) {
        return values.containsKey(name);
    }

    public boolean isEmpty() {
        return Utils.CL.isEmpty(values);
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public Object remove(String name) {
        return values.remove(name);
    }

    public Object remove(TypedAttribute<?> field) {
        return values.remove(field.getFieldName());
    }

    public void clear() {
        values.clear();
    }

    public Map<String, Object> asMap() {
        return Collections.unmodifiableMap(values);
    }

    public Map<String, Object> getValues() {
        return values;
    }

    private String getRefName(TypedAttribute<?> field) {
        return Utils.STR.format(Constants.ATTRIBUTE_REF_TEMPLATE, field.getFieldName());
    }

    public void putAll(DataRecord record) {
        this.values.putAll(record.values);
    }

    public void putAll(Map<String, Object> records) {
        this.values.putAll(records);
    }

    //static helper method
    public static DataRecord of(Map<String, Object> map) {
        return new DataRecord(map);
    }

    public DataRecord copy() {
        return new DataRecord(new HashMap<>(values));
    }

    public static DataRecord copyOf(Map<String, Object> map) {
        return new DataRecord(new HashMap<>(map));
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


    public boolean hasValue(TypedAttribute<?> field) {
        return hasValue(field.getFieldName());
    }

    public boolean hasValue(String name) {
        Object value = Utils.CL.emptyIfNull(values).get(name);
        return value != null;
    }

    public boolean isNull(TypedAttribute<?> field) {
        return isNull(field.getFieldName());
    }

    public boolean isNull(String name) {
        return !hasValue(name);
    }

    public boolean isNotNull(TypedAttribute<?> field) {
        return isNotNull(field.getFieldName());
    }

    public boolean isNotNull(String name) {
        return !isNull(name);
    }

}