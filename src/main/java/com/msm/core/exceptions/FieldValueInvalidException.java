package com.msm.core.exceptions;

import java.util.Map;

public class FieldValueInvalidException extends GenericBaseException {
    public static final String FIELD_PARAM = "field";
    private static final String DEFAULT_MESSAGE = "Field invalid";

    public FieldValueInvalidException(String fieldName, Throwable cause) {
        super(ErrorCodeEnum.INVALID_ARGUMENT, DEFAULT_MESSAGE, Map.of(FIELD_PARAM, fieldName), cause);
    }

    public FieldValueInvalidException(String fieldName, String message, Throwable cause) {
        super(ErrorCodeEnum.INVALID_ARGUMENT, message, Map.of(FIELD_PARAM, fieldName), cause);
    }

    public FieldValueInvalidException(String fieldName) {
        super(ErrorCodeEnum.INVALID_ARGUMENT, DEFAULT_MESSAGE, Map.of(FIELD_PARAM, fieldName));
    }

    public FieldValueInvalidException(String fieldName, String msg) {
        super(ErrorCodeEnum.INVALID_ARGUMENT, msg, Map.of(FIELD_PARAM, fieldName));
    }
}