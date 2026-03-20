package com.msm.core.validate;

import com.msm.core.validate.domain.AttributeType;

import java.time.LocalDate;

public interface AttributeTypeHandler {

    boolean supports(Class<?> t);

    String dataType();

    boolean isValid(Object value);

    Object normalizeValue(Object value); // convert về đúng kiểu
}