package com.msm.core.dynamicquery;

import com.fasterxml.jackson.databind.JavaType;
import com.msm.core.exceptions.CommonErrors;
import com.msm.core.metadata.Attribute;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
public class ConditionUtils {

    public static Condition buildArrayContainsCondition(Attribute attribute, Object inputValue) {
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
            } else if (normalizedValue != null && normalizedValue.getClass().isArray()) {
                arrayValue = (Object[]) normalizedValue;
            } else {
                arrayValue = new Object[]{normalizedValue};
            }

            JavaType contentType = attribute.getJavaType().getContentType();
            if (contentType != null && contentType.getRawClass().equals(String.class)) {
                return DSL.condition(
                        "{0} @> {1}::text[]",
                        field,
                        DSL.val(arrayValue)
                );
            }
            return DSL.condition(
                    "{0} @> {1}",
                    field,
                    DSL.val(arrayValue)
            );
        } else {
            Field<Object> field = (Field<Object>) attribute.getField();
            return field.eq(normalizedValue);
        }
    }

    public static Condition buildArrayOverlapCondition(Attribute attribute, Object inputValue) {
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
            } else if (normalizedValue != null && normalizedValue.getClass().isArray()) {
                arrayValue = (Object[]) normalizedValue;
            } else {
                arrayValue = new Object[]{normalizedValue};
            }

            JavaType contentType = attribute.getJavaType().getContentType();
            if (contentType != null && contentType.getRawClass().equals(String.class)) {
                return DSL.condition(
                        "{0} && {1}::text[]",
                        field,
                        DSL.val(arrayValue)
                );
            }

            return DSL.condition(
                    "{0} && {1}",
                    field,
                    DSL.val(arrayValue)
            );
        } else {
            Field<Object> field = (Field<Object>) attribute.getField();
            return field.eq(normalizedValue);
        }
    }

    /**
     * Value compared for (in)equality with a JSON path: {@code ->>} returns text, so anything but a string (UUID,
     * number, boolean, enum) is compared as its text.
     */
    public static Object jsonText(Object value) {
        return value == null || value instanceof String ? value : value.toString();
    }

    /** {@link #jsonText} of each value of a collection. */
    public static List<Object> jsonTexts(Collection<?> values) {
        return values.stream().map(ConditionUtils::jsonText).toList();
    }

    /**
     * A JSON path compared with an order operator ({@code > >= < <=}): cast to numeric for a number, to timestamptz
     * for an instant / offset date time, as BETWEEN does; compared as text otherwise.
     */
    @SuppressWarnings("unchecked")
    public static Field<Object> jsonComparable(Field<?> extracted, Object value) {
        if (value instanceof Number) {
            return (Field<Object>) (Field<?>) extracted.cast(SQLDataType.NUMERIC);
        }
        if (value instanceof Instant) {
            return (Field<Object>) (Field<?>) extracted.cast(SQLDataType.INSTANT);
        }
        if (value instanceof OffsetDateTime) {
            return (Field<Object>) (Field<?>) extracted.cast(SQLDataType.OFFSETDATETIME);
        }
        return (Field<Object>) extracted;
    }

    /** Value compared with {@link #jsonComparable}: numbers and date times as they are, anything else as text. */
    public static Object jsonOrderValue(Object value) {
        return value instanceof Number || value instanceof Instant || value instanceof OffsetDateTime ? value : jsonText(value);
    }

    public static Condition unaccentLike(Field<String> field, String keyword) {
        String pattern = "%" + keyword.toLowerCase() + "%";
        Field<String> left = DSL.function("unaccent", String.class, DSL.lower(field));
        Field<String> right = DSL.function("unaccent", String.class, DSL.inline(pattern));

        return left.like(right);
    }

    public static void requireWhereCondition(Condition condition) {
        if (condition == null) {
            throw CommonErrors.missingWhereConditionException("WHERE condition must not be null");
        }

        if (condition.equals(DSL.noCondition())) {
            throw CommonErrors.missingWhereConditionException("WHERE condition must not be empty");
        }
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
