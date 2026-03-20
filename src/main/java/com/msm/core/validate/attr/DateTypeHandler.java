package com.msm.core.validate.attr;

import com.msm.core.validate.AttributeTypeHandler;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateTypeHandler implements AttributeTypeHandler {

    static {
        AttributeTypeHandlerFactory.register(new DateTypeHandler());
    }
    private DateTypeHandler(){}


    @Override
    public boolean supports(Class<?> t) {
        return t == LocalDate.class;
    }

    @Override
    public String dataType() {
        return "LocalDate";
    }

    @Override
    public boolean isValid(Object value) {
        try {
            LocalDate.parse(value.toString(), DateTimeFormatter.ISO_DATE);
            return true;
        } catch (Exception e) {
            log.warn("Can't parse LocalDate value:{}", value);
            return false;
        }
    }

    @Override
    public Object normalizeValue(Object value) {
        return LocalDate.parse(value.toString(), DateTimeFormatter.ISO_DATE);
    }
}