package com.msm.core.metadata.typesafe;

import com.msm.core.commons.Utils;
import lombok.Getter;
import org.jooq.Field;

@Getter
public class TypedAttribute<T> {

    private final String fieldName;
    private final Class<T> javaType;
    private final Field<T> field;

    public TypedAttribute(String fieldName, Class<T> javaType, Field<T> field) {
        this.fieldName = fieldName;
        this.javaType = javaType;
        this.field = field;
    }

    public TypedAttribute(String fieldName, Class<T> javaType) {
        this.fieldName = fieldName;
        this.javaType = javaType;
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
