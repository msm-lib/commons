package com.msm.core.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"unchecked"})
public final class ObjectUtils {
    private final ObjectPropertyUtils PROPS = new ObjectPropertyUtils();
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS = Map.of(
            int.class, Integer.class,
            long.class, Long.class,
            double.class, Double.class,
            boolean.class, Boolean.class,
            float.class, Float.class,
            short.class, Short.class,
            byte.class, Byte.class,
            char.class, Character.class
    );
    private static final Map<Class<?>, Function<String, ?>> CONVERTERS = Map.of(
            Integer.class, Integer::valueOf,
            Long.class, Long::valueOf,
            Double.class, Double::valueOf,
            BigDecimal.class, BigDecimal::new,
            Boolean.class, Boolean::valueOf,
            LocalDate.class, LocalDate::parse,
            LocalDateTime.class, LocalDateTime::parse,
            UUID.class, UUID::fromString
    );

    public Class<?> normalize(Class<?> type) {
        return type.isPrimitive() ? PRIMITIVE_WRAPPERS.get(type) : type;
    }

    public Function<String, ?> getCastFunction(Class<?> targetType) {
        Class<?> normalized = normalize(targetType);
        return CONVERTERS.get(normalized);
    }

    public <T> T cast(Class<?> targetType, Object value) {
        if (Objects.isNull(value)) return null;
        if (targetType.isInstance(value)) {
            return (T) value;
        }
        if (targetType.isEnum()) {
            return (T) Enum.valueOf((Class<? extends Enum>) targetType, value.toString());
        }
        Function<String, ?> fn = getCastFunction(targetType);
        if (Objects.nonNull(fn)) {
            return (T) fn.apply(value.toString());
        }

        throw new IllegalArgumentException("Cannot cast value '" + value + "' to type " + targetType.getName());
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

    public <K, V> Map<K, V> toMap(Object object) throws JsonProcessingException {
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

    ObjectUtils() {}
}
