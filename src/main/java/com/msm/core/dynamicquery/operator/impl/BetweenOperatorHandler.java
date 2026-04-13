package com.msm.core.dynamicquery.operator.impl;

import com.msm.core.dynamicquery.operator.AbstractOperatorHandler;
import com.msm.core.exceptions.Errors;
import com.msm.core.filter.domain.FilterCondition;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings({"unchecked"})
public class BetweenOperatorHandler extends AbstractOperatorHandler {


    @Override
    protected Condition doHandle(Field<?> field, FilterCondition condition) {
        return buildBetweenCondition(field, condition);
    }

    private Condition buildBetweenCondition(Field<?> field, Object value) {
        if (!(value instanceof List<?> values) || values.size() != 2) {
            throw Errors.invalid("BETWEEN requires exactly 2 values");
        }

        Object from = values.get(0);
        Object to = values.get(1);

        // detect type & cast
        if (field.getType() == String.class) {
            return ((Field<String>) field).between(from.toString(), to.toString());
        }

        if (Number.class.isAssignableFrom(field.getType())) {
            return ((Field<BigDecimal>) field).between(
                    new BigDecimal(from.toString()),
                    new BigDecimal(to.toString())
            );
        }

        if (field.getType() == java.time.LocalDate.class) {
            return ((Field<LocalDate>) field).between(
                    LocalDate.parse(from.toString()),
                    LocalDate.parse(to.toString())
            );
        }

        if (field.getType() == java.time.LocalDateTime.class) {
            return ((Field<LocalDateTime>) field).between(
                    LocalDateTime.parse(from.toString()),
                    LocalDateTime.parse(to.toString())
            );
        }

        //2024-01-01T10:15:30+07:00
        //2024-01-01T03:15:30Z
        if (field.getType() == OffsetDateTime.class) {
            return ((Field<OffsetDateTime>) field).between(
                    OffsetDateTime.parse(from.toString()),
                    OffsetDateTime.parse(to.toString())
            );
        }

        // fallback
        return DSL.condition("{0} between {1} and {2}", field, from, to);
    }

    private <T extends Comparable<T>> Condition between(
            Field<T> field, Object from, Object to, Function<String, T> parser) {

        return field.between(
                parser.apply(from.toString()),
                parser.apply(to.toString())
        );
    }
}