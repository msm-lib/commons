package com.msm.core.dynamicquery.operator;

import com.msm.core.dynamicquery.mapping.JavaTypeMappingFactory;
import com.msm.core.metadata.Attribute;
import org.jooq.Condition;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.jooq.util.postgres.PostgresDSL;

import java.util.List;
import java.util.Set;

public class ConditionUtils {

    public static Condition buildArrayContainsCondition(Attribute attribute, Object inputValue) {
        DataType<?> jooqType = JavaTypeMappingFactory.getDataType(attribute.getFieldType());
        Object normalizedValue = attribute.castAndAcceptCollectionAsArray(inputValue);

        if (attribute.getFieldType().contains("[]")
                || attribute.getFieldType().contains("List")
                || attribute.getFieldType().contains("Set")
                || attribute.getFieldType().startsWith("_")) {
            Field<Object[]> field = (Field<Object[]>) attribute.getField();
            Object[] arrayValue;
            if (normalizedValue instanceof List) {
                arrayValue = ((List<?>) normalizedValue).toArray();
            } else if (normalizedValue instanceof Set) {
                arrayValue = ((Set<?>) normalizedValue).toArray();
            } else {
                arrayValue = (Object[]) normalizedValue;
            }

            Field<Object> valueField = (Field<Object>) DSL.value(arrayValue, jooqType);
            return PostgresDSL.arrayContains(
                    field,
                    valueField);
        } else {
            Field<Object> field = (Field<Object>) attribute.getField();
            return field.eq(normalizedValue);
        }
    }

    public static Condition buildArrayOverlapCondition(Attribute attribute, Object inputValue) {
        DataType<?> jooqType = JavaTypeMappingFactory.getDataType(attribute.getFieldType());
        Object normalizedValue = attribute.castAndAcceptCollectionAsArray(inputValue);
        if (attribute.getFieldType().contains("[]")
                || attribute.getFieldType().contains("List")
                || attribute.getFieldType().contains("Set")
                || attribute.getFieldType().startsWith("_")) {
            Field<Object[]> field = (Field<Object[]>) attribute.getField();
            Object[] arrayValue;
            if (normalizedValue instanceof List) {
                arrayValue = ((List<?>) normalizedValue).toArray();
            } else if (normalizedValue instanceof Set) {
                arrayValue = ((Set<?>) normalizedValue).toArray();
            } else {
                arrayValue = (Object[]) normalizedValue;
            }

            return PostgresDSL.arrayOverlap(
                    field,
                    DSL.value(arrayValue, (DataType<Object[]>) jooqType));
        } else {
            Field<Object> field = (Field<Object>) attribute.getField();
            return field.eq(normalizedValue);
        }
    }

    public static Condition unaccentLike(Field<String> field, String keyword) {

        String pattern = "%" + keyword.toLowerCase() + "%";
        Field<String> left = DSL.function("unaccent", String.class, DSL.lower(field));
        Field<String> right = DSL.function("unaccent", String.class, DSL.inline(pattern));

        return left.like(right);
    }

//    public static DataType<?> resolveJooqDataType(String typeName) {
//        if (typeName == null) return SQLDataType.OTHER;
//
//        String type = typeName.toLowerCase().trim();
//        boolean isArray = type.contains("[]") || type.contains("list") || type.contains("set") || type.startsWith("_");
//
//        if (type.contains("uuid")) {
//            return isArray ? SQLDataType.UUID.array() : SQLDataType.UUID;
//        }
//
//        if (type.contains("string") || type.contains("text") || type.contains("varchar")) {
//            return isArray ? SQLDataType.VARCHAR.array() : SQLDataType.VARCHAR;
//        }
//
//        if (type.contains("int") || type.contains("integer")) {
//            return isArray ? SQLDataType.INTEGER.array() : SQLDataType.INTEGER;
//        }
//
//        if (type.contains("long") || type.contains("bigint")) {
//            return isArray ? SQLDataType.BIGINT.array() : SQLDataType.BIGINT;
//        }
//
//        if (type.contains("boolean") || type.contains("bool")) {
//            return isArray ? SQLDataType.BOOLEAN.array() : SQLDataType.BOOLEAN;
//        }
//
//        if (type.contains("instant") || type.contains("timestamp")) {
//            return isArray ? SQLDataType.INSTANT.array() : SQLDataType.INSTANT;
//        }
//
//        if (type.contains("localdate") || type.contains("date")) {
//            return isArray ? SQLDataType.LOCALDATE.array() : SQLDataType.LOCALDATE;
//        }
//
//        if (type.contains("decimal") || type.contains("numeric")) {
//            return isArray ? SQLDataType.NUMERIC.array() : SQLDataType.NUMERIC;
//        }
//
//        if (type.contains("double") || type.contains("float8")) {
//            return isArray ? SQLDataType.DOUBLE.array() : SQLDataType.DOUBLE;
//        }
//
//        return isArray ? SQLDataType.OTHER.array() : SQLDataType.OTHER;
//    }
}
