package com.msm.core.validate.attr;

import com.msm.core.validate.AttributeTypeHandler;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

@Slf4j
public class DateTimeTypeHandler implements AttributeTypeHandler {

    static {
        AttributeTypeHandlerFactory.register(new DateTimeTypeHandler());
    }
    private DateTimeTypeHandler(){}


    @Override
    public boolean supports(Class<?> t) {
        return Temporal.class.isAssignableFrom(t);
    }

    @Override
    public String dataType() {
        return "LocalDateTime";
    }

    @Override
    public boolean isValid(Object value) {
        try {
            LocalDateTime.parse(value.toString(), DateTimeFormatter.ISO_DATE_TIME);
            return true;
        } catch (Exception e) {
            log.warn("Can't parse LocalDateTime value:{}", value);
            return false;
        }
    }

    @Override
    public Object normalizeValue(Object value) {
        return LocalDateTime.parse(value.toString(), DateTimeFormatter.ISO_DATE_TIME);
    }
}