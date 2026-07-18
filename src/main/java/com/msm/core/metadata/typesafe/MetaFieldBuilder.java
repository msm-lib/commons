package com.msm.core.metadata.typesafe;

import com.fasterxml.jackson.databind.JavaType;
import com.msm.core.dynamicquery.mapping.JavaTypeMappingFactory;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
public final class MetaFieldBuilder {
    private static final String CUSTOM_VALUE_FIELD_NAME = "custom_values";
    private MetaFieldBuilder() {}

    public static <T> TypedAttribute<T> attr(Table<?> table, String fieldName, String columnName, Class<T> type) {
        Field<T> field = DSL.field(DSL.name(table.getName(), columnName), type);
        return new TypedAttribute<>(
                fieldName,
                type,
                field
        );
    }

    public static <T> TypedAttribute<T> attr(Table<?> table, String fieldName, String columnName, DataType<T> dataType) {
        Field<T> field = DSL.field(DSL.name(table.getName(), columnName), dataType);
        return new TypedAttribute<>(
                fieldName,
                dataType.getType(),
                field
        );
    }

    public static <T> TypedAttribute<T> attr(String fieldName, Class<T> type) {
        return new TypedAttribute<>(
                fieldName,
                type
        );
    }

    public static <T> TypedAttribute<T> attrRef(Table<?> table, String referenceName, Class<T> type) {

        Field<T> field = (Field<T>) DSL.field(
                "({0}->{1})",
                SQLDataType.JSONB.asConvertedDataType(JavaTypeMappingFactory.DEFAULT_JSONB_CONVERTER),
                DSL.field(DSL.name(table.getName(), CUSTOM_VALUE_FIELD_NAME)),
                DSL.inline(referenceName)
        );

        return new TypedAttribute<>(
                referenceName,
                type,
                field
        );
    }

    public static <T> TypedAttribute<T> attrRef(Table<?> table, String referenceName, Class<T> type, JavaType ... params) {

        Field<T> field = (Field<T>) DSL.field(
                "({0}->{1})",
                SQLDataType.JSONB.asConvertedDataType(JavaTypeMappingFactory.createConverter(type, params)),
                DSL.field(DSL.name(table.getName(), CUSTOM_VALUE_FIELD_NAME)),
                DSL.inline(referenceName)
        );

        return new TypedAttribute<>(
                referenceName,
                type,
                field
        );
    }

    public static <T> TypedAttribute<T> attrRef(Table<?> table, String referenceName, JavaType type) {

        Field<T> field = (Field<T>) DSL.field(
                "({0}->{1})",
                SQLDataType.JSONB.asConvertedDataType(JavaTypeMappingFactory.createConverter(type)),
                DSL.field(DSL.name(table.getName(), CUSTOM_VALUE_FIELD_NAME)),
                DSL.inline(referenceName)
        );

        return new TypedAttribute<>(
                referenceName,
                type,
                field
        );
    }

    public static Field<?>[] fields(TypedAttribute<?>... attrs) {
        return Arrays.stream(attrs)
                .map(TypedAttribute::getField)
                .toArray(Field[]::new);
    }

    public static Field<?>[] fields(List<TypedAttribute<?>> attrs) {
        return attrs.stream()
                .map(TypedAttribute::getField)
                .toArray(Field[]::new);
    }

    public static Field<?>[] fieldsAlias(TypedAttribute<?>... attrs) {
        return Arrays.stream(attrs)
                .map(TypedAttribute::as)
                .toArray(Field[]::new);
    }

    public static Field<?>[] fieldsAlias(List<TypedAttribute<?>> attrs) {
        return attrs.stream()
                .map(TypedAttribute::as)
                .toArray(Field[]::new);
    }
}