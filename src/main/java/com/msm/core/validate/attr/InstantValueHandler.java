package com.msm.core.validate.attr;

import com.msm.core.metadata.Attribute;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeParseException;
import java.util.Objects;


@Slf4j
public final class InstantValueHandler implements ValueValidationHandler {

    @Override
    public String supportType() {
        return "Instant";
    }

    @Override
    public boolean isValid(Attribute attribute, Object value) {
        if(Objects.isNull(value)) {
            return true;
        }

        try {
            attribute.cast(value);
            return true;
        } catch (DateTimeParseException ignore) {
            return false;
        }
    }
}