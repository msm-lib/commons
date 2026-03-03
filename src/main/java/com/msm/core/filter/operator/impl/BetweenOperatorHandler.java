package com.msm.core.filter.operator.impl;


import com.msm.core.commons.Utils;
import com.msm.core.filter.domain.DateRange;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.filter.operator.AbstractComparableHandler;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@SuppressWarnings({"rawtypes", "unchecked"})
public class BetweenOperatorHandler extends AbstractComparableHandler {

    @Override
    protected BooleanExpression doCompare(ComparableExpression exp, Object value, FilterCondition c) {

        DateRange range = Utils.convertObject(value, DateRange.class);
        String timeZone = Utils.defaultIfNull(range.getTimezone(), () -> "UTC");
        ZoneId zone = ZoneId.of(timeZone);
        LocalDateTime from = LocalDateTime.parse(range.getFrom());
        LocalDateTime to = LocalDateTime.parse(range.getTo());
        LocalDateTime sysFrom = from.atZone(zone)
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime sysTo = to.atZone(zone)
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime();

        return exp.between(sysFrom, sysTo);
    }

    private Comparable convert(String value, Class<?> type, ZoneId zone) {
        if (type == LocalDateTime.class) {
            return LocalDateTime
                    .parse(value)
                    .atZone(zone)
                    .withZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDateTime();
        }

        if (type == Instant.class) {
            return LocalDateTime
                    .parse(value)
                    .atZone(zone)
                    .toInstant();
        }

        if (type == OffsetDateTime.class) {
            return LocalDateTime
                    .parse(value)
                    .atZone(zone)
                    .toOffsetDateTime();
        }

        throw new IllegalArgumentException("BETWEEN not supported for type " + type);
    }
}