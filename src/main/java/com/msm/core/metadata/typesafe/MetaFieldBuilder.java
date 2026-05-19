package com.msm.core.metadata.typesafe;

import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

public final class MetaFieldBuilder {

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
}