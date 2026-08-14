package com.msm.core.metadata.typesafe;

import com.fasterxml.jackson.databind.JavaType;
import com.msm.core.commons.GenericTypeResolverFactory;
import com.msm.core.commons.Utils;
import lombok.Getter;
import org.jooq.Field;
import org.jooq.Table;

@SuppressWarnings({"unchecked"})
@Getter
public class TypedAttribute<T> {

    private final String fieldName;
    private final JavaType javaType;
    private final Class<T> clazz;
    private final Field<T> field;
    private final ValueResolver<T> valueResolver;

    public TypedAttribute(String fieldName, Class<T> clazz, Field<T> field) {
        this(fieldName, clazz, field, null);
    }

    public TypedAttribute(
            String fieldName,
            Class<T> clazz,
            Field<T> field,
            ValueResolver<T> valueResolver
    ) {
        this.fieldName = fieldName;
        this.clazz = clazz;
        this.javaType = GenericTypeResolverFactory.resolve(clazz);
        this.field = field;
        this.valueResolver = valueResolver;
    }

    public TypedAttribute(String fieldName, JavaType javaType, Field<T> field) {
        this(fieldName, javaType, field, null);
    }

    public TypedAttribute(String fieldName, JavaType javaType, Field<T> field, ValueResolver<T> valueResolver) {
        this.fieldName = fieldName;
        this.javaType = javaType;
        this.clazz = (Class<T>) javaType.getRawClass();
        this.field = field;
        this.valueResolver = valueResolver;
    }

    public TypedAttribute(String fieldName, Class<T> clazz) {
        this(fieldName, clazz, (ValueResolver<T>)null);
    }

    public TypedAttribute(String fieldName, Class<T> clazz, ValueResolver<T> valueResolver) {
        this.fieldName = fieldName;
        this.clazz = clazz;
        this.javaType = GenericTypeResolverFactory.resolve(clazz);
        this.field = null;
        this.valueResolver = valueResolver;
    }

    public TypedAttribute(String fieldName, JavaType javaType) {
        this(fieldName, javaType, (ValueResolver<T>)null);
    }

    public TypedAttribute(String fieldName, JavaType javaType, ValueResolver<T> valueResolver) {
        this.fieldName = fieldName;
        this.javaType = javaType;
        this.clazz = (Class<T>) javaType.getRawClass();
        this.field = null;
        this.valueResolver = valueResolver;
    }

    public boolean isComputed() {
        return valueResolver != null;
    }

    public T resolve(DataRecord record) {
        if (valueResolver != null) {
            return valueResolver.resolve(record);
        }

        return cast(record.getValues().get(fieldName));
    }

    public Field<T> as(String alias) {
        if (field == null) return null;
        return field.as(alias);
    }

    public Field<T> as() {
        if (field == null) return null;
        return field.as(getFieldName());
    }

    public Field<T> as(Table<?> table) {
        if (field == null) return null;
        return table.field(field.getName(), field.getDataType());
    }

    public Field<T> as(Table<?> table, String alias) {
        Field<T> field = as(table);
        if (field == null) return null;
        return field.as(alias);
    }

    public T cast(Object value) {
        if (value == null) {
            return null;
        }
        return Utils.O.convertToType(value, javaType);
    }

    public static <T> TypedAttribute<T> computed(
            String fieldName,
            Class<T> clazz,
            ValueResolver<T> resolver
    ) {
        return new TypedAttribute<>(fieldName, clazz, resolver);
    }

    @Override
    public String toString() {
        return fieldName;
    }
}
