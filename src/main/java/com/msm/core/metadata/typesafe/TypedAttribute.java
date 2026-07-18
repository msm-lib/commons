package com.msm.core.metadata.typesafe;

import com.fasterxml.jackson.databind.JavaType;
import com.msm.core.commons.GenericTypeResolverFactory;
import com.msm.core.commons.Utils;
import lombok.Getter;
import org.jooq.Field;

@Getter
public class TypedAttribute<T> {

    private final String fieldName;
    private final JavaType javaType;
    private final Class<T> clazz;
    private final Field<T> field;

    public TypedAttribute(String fieldName, Class<T> clazz, Field<T> field) {
        this.fieldName = fieldName;
        this.clazz = clazz;
        this.javaType = GenericTypeResolverFactory.resolve(clazz);
        this.field = field;
    }

    public TypedAttribute(String fieldName, JavaType javaType, Field<T> field) {
        this.fieldName = fieldName;
        this.javaType = javaType;
        this.clazz = (Class<T>) javaType.getRawClass();
        this.field = field;
    }

    public TypedAttribute(String fieldName, Class<T> clazz) {
        this.fieldName = fieldName;
        this.clazz = clazz;
        this.javaType = GenericTypeResolverFactory.resolve(clazz);
        this.field = null;
    }

    public TypedAttribute(String fieldName, JavaType javaType) {
        this.fieldName = fieldName;
        this.javaType = javaType;
        this.clazz = (Class<T>) javaType.getRawClass();
        this.field = null;
    }

    public Field<T> as(String alias) {
        if (field == null) return null;
        return field.as(alias);
    }

    public Field<T> as() {
        if (field == null) return null;
        return field.as(getFieldName());
    }

    public T cast(Object value) {
        if (value == null) {
            return null;
        }
        return Utils.O.convertToType(value, javaType);
    }

    @Override
    public String toString() {
        return fieldName;
    }
}
