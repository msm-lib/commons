package com.msm.core.validate.attr;

import com.msm.core.validate.domain.Attribute;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

@Slf4j
public final class DateTimeValueHandler implements ValueValidationHandler {

    @Override
    public String supportType() {
        return "DateTime";
    }

    @Override
    public boolean isValid(Attribute attribute, Object value) {
        if(Objects.isNull(value)) {
            return true;
        }
        try {
            LocalDateTime.parse((CharSequence) value);
            return true;
        } catch (DateTimeParseException ignore) {
            return false;
        }
    }
}