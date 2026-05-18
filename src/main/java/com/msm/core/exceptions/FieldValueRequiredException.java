package com.msm.core.exceptions;

import java.util.Map;

public class FieldValueRequiredException extends GenericBaseException {
    public static final String FIELD_PARAM = "field";
    private static final String DEFAULT_MESSAGE = "Field is required";

    public FieldValueRequiredException(String fieldName, Throwable cause) {
        super(ErrorCodeEnum.FIELD_VALUE_REQUIRED, DEFAULT_MESSAGE, Map.of(FIELD_PARAM, fieldName), cause);
    }

    public FieldValueRequiredException(String fieldName, String message, Throwable cause) {
        super(ErrorCodeEnum.FIELD_VALUE_REQUIRED, message, Map.of(FIELD_PARAM, fieldName), cause);
    }

    public FieldValueRequiredException(String fieldName) {
        super(ErrorCodeEnum.FIELD_VALUE_REQUIRED, DEFAULT_MESSAGE, Map.of(FIELD_PARAM, fieldName));
    }

    public FieldValueRequiredException(String fieldName, String msg) {
        super(ErrorCodeEnum.FIELD_VALUE_REQUIRED, msg, Map.of(FIELD_PARAM, fieldName));
    }
}
