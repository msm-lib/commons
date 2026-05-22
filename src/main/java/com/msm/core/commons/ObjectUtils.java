package com.msm.core.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings({"unchecked"})
public final class ObjectUtils {
    private final ObjectPropertyUtils PROPS = new ObjectPropertyUtils();
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .registerModule(new JavaTimeModule())
            .setDefaultMergeable(false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public <T> T updateValues(T object, Map<String, Object> updates) throws JsonMappingException {
        return MAPPER.updateValue(object, updates);
    }

    public <T> T read(InputStream src, TypeReference<T> valueTypeRef) throws IOException {
        return MAPPER.readValue(src, valueTypeRef);
    }

    public <T> T read(String src, TypeReference<T> valueTypeRef) throws IOException {
        return MAPPER.readValue(src, valueTypeRef);
    }

    public <T> T convertObject(Object object, Class<T> clazz) {
        if (Objects.isNull(object)) {
            return null;
        }

        return MAPPER.convertValue(object,clazz);
    }

    public <T> T toObject(Map<String, Object> attributeMap, Class<T> clazz) {
        if (Objects.isNull(attributeMap)) {
            return null;
        }
        return MAPPER.convertValue(attributeMap, clazz);
    }

    public <K, V> Map<K, V> toMap(Object object) {
        if (Objects.isNull(object)) {
            return null;
        }

        return MAPPER.convertValue(object, Map.class);
    }

    public <T> T toObject(String object, Class<T> clazz) throws JsonProcessingException {
        if (Objects.isNull(object)) {
            return null;
        }
        return MAPPER.readValue(object, clazz);
    }

    public <T> T toObject(String object, JavaType type) throws JsonProcessingException {
        if (Objects.isNull(object)) {
            return null;
        }
        return MAPPER.readValue(object, type);
    }

    public <T> String toJsonString(T value) throws JsonProcessingException {
        if (Objects.isNull(value)) {
            return null;
        }
        return MAPPER.writeValueAsString(value);
    }

    public <T> List<T> toListObject(String object, Class<T> clazz) throws JsonProcessingException {
        if (Objects.isNull(object)) {
            return null;
        }
        CollectionType collectionType = MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
        return MAPPER.readValue(object, collectionType);
    }

    public <T> String toJsonString(List<T> list) throws JsonProcessingException {
        if (Objects.isNull(list)) {
            return null;
        }
        return MAPPER.writeValueAsString(list);
    }

    public <T> T getSupplier(final Supplier<T> supplier) {
        return Objects.isNull(supplier) ? null : supplier.get();
    }

    public <T> T defaultIfNull(T value, Supplier<T> defaultSupplier) {
        if (Objects.isNull(value)) {
            return getSupplier(defaultSupplier);
        }
        return value;
    }

    public Object getProperty(Object root, String path) {
        return PROPS.getProperty(root, path);
    }

    public void setProperty(Object root, String path, Object value) {
        PROPS.setProperty(root, path, value);
    }

    public <T> T[] convertListToArray(List<T> value, Class<?> clazz) {
        if (value == null) return null;
        ArrayType arrayType = MAPPER.getTypeFactory().constructArrayType(clazz);
        return MAPPER.convertValue(value, arrayType);
    }

    public <T> T convertToType(Object value, String typeName) {
        return convertToType(value, typeName, false);
    }

    public <T> T convertToType(Object value, String typeName, boolean acceptCollectionAsArray) {
        if (value == null) return null;
        JavaType targetType = GenericTypeResolverFactory.resolve(typeName);
        try {
            boolean isArrayLike = targetType.isCollectionLikeType() || targetType.isArrayType();
            if (acceptCollectionAsArray && isArrayLike) {
                // Get type of Collection (exp: UUID, String, Instant)
                JavaType contentType = targetType.getContentType();
                // Create JavaType array to convert
                JavaType arrayType = MAPPER.getTypeFactory().constructArrayType(contentType);
                Object convertedArray = MAPPER.convertValue(value, arrayType);
                return (T) convertedArray;
            }
            return MAPPER.convertValue(value, targetType);
        } catch (IllegalArgumentException e) {
            if (value instanceof String) {
                try {
                    return MAPPER.readValue((String) value, targetType);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        }
    }

    public <T> T convertToType(Object value, Class<T> clazz) {
        if (value == null) return null;
        try {
            return MAPPER.convertValue(value, clazz);
        } catch (IllegalArgumentException e) {
            if (value instanceof String) {
                try {
                    return MAPPER.readValue((String) value, clazz);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        }
    }

    public <K, V> Map<K, V> convertToMap(Object value, Class<K> keyClass, Class<V> valueClass) {
        if (value == null) return null;
        MapType mapType = MAPPER.getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
        try {
            return MAPPER.convertValue(value, mapType);
        } catch (IllegalArgumentException e) {
            if (value instanceof String) {
                try {
                    return MAPPER.readValue((String) value, mapType);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        }
    }

    ObjectUtils() {}
}
