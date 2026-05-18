package com.msm.core.dynamicquery.operator.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.msm.core.commons.Utils;
import com.msm.core.dynamicquery.FieldResolver;
import com.msm.core.dynamicquery.operator.OperatorHandler;
import com.msm.core.exceptions.CommonErrors;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.SQLDataType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@SuppressWarnings({"unchecked"})
public class BetweenOperatorHandler implements OperatorHandler {

    @Override
    public Condition handle(ObjectMetadata objectMetadata, FilterCondition condition) {
        String fieldName = FieldResolver.pathAsFieldName(condition.getField());
        Attribute attribute = objectMetadata.getAttributeByName(fieldName);
        if(attribute == null) {
            throw CommonErrors.fieldNotFoundException(fieldName, fieldName + " not found");
        }
        Object value = condition.getValue();
        if (!(value instanceof List<?> values) || values.size() != 2) {
            throw CommonErrors.unsupported("BETWEEN", "BETWEEN requires exactly 2 values");
        }

        if(attribute.isJsonField()) {
            Field<Object> field = (Field<Object>) FieldResolver.resolve(objectMetadata, condition.getField());
            return buildJsonbBetween(field, values.getFirst(), values.getLast());
        }

        Field<?> field = attribute.getField();
        JavaType attrJavaType = attribute.getJavaType();
        Object from = attribute.cast(values.getFirst());
        Object to = attribute.cast(values.getLast());

        // detect type & cast
        if (attrJavaType.isTypeOrSubTypeOf(String.class)) {
            return ((Field<String>) field).between(from.toString(), to.toString());
        }

        if (attrJavaType.isTypeOrSubTypeOf(Number.class)) {
            return ((Field<BigDecimal>) field).between(
                    new BigDecimal(from.toString()),
                    new BigDecimal(to.toString())
            );
        }

        if (attrJavaType.isTypeOrSubTypeOf(LocalDate.class)) {
            return ((Field<LocalDate>) field).between(
                    LocalDate.parse(from.toString()),
                    LocalDate.parse(to.toString())
            );
        }

        if (attrJavaType.isTypeOrSubTypeOf(LocalDateTime.class)) {
            return ((Field<LocalDateTime>) field).between(
                    LocalDateTime.parse(from.toString()),
                    LocalDateTime.parse(to.toString())
            );
        }

        if (attrJavaType.isTypeOrSubTypeOf(OffsetDateTime.class)) {
            return ((Field<OffsetDateTime>) field).between(
                    OffsetDateTime.parse(from.toString()),
                    OffsetDateTime.parse(to.toString())
            );
        }

        if (attrJavaType.isTypeOrSubTypeOf(Instant.class)) {
            return ((Field<OffsetDateTime>) field).between(
                    OffsetDateTime.parse(from.toString()),
                    OffsetDateTime.parse(to.toString())
            );
        }

        throw CommonErrors.unsupported("BETWEEN", "BETWEEN operator unsupported for the field '"
                + condition.getField() + "' with values[" + from + ", " + to + "]");
    }

    public Condition buildJsonbBetween(Field<?> extracted, Object start, Object end) {
        if (start instanceof Number) {
            BigDecimal startCasted = Utils.O.convertToType(start, BigDecimal.class.getSimpleName());
            BigDecimal endCasted = Utils.O.convertToType(end, BigDecimal.class.getSimpleName());
            return extracted.cast(SQLDataType.NUMERIC).between(startCasted, endCasted);
        } else if (start instanceof Instant) {
            Instant startCasted = Utils.O.convertToType(start, Instant.class.getSimpleName());
            Instant endCasted = Utils.O.convertToType(end, Instant.class.getSimpleName());
            return extracted.cast(SQLDataType.INSTANT).between(startCasted, endCasted);
        } else if (start instanceof OffsetDateTime) {
            OffsetDateTime startCasted = Utils.O.convertToType(start, OffsetDateTime.class.getSimpleName());
            OffsetDateTime endCasted = Utils.O.convertToType(end, OffsetDateTime.class.getSimpleName());
            return extracted.cast(SQLDataType.OFFSETDATETIME).between(startCasted, endCasted);
        } else {
            return ((Field<String>) extracted).between(String.valueOf(start), String.valueOf(end));
        }
    }
}