package com.msm.core.exceptions;

import java.util.Map;

public class FieldNotFoundException extends GenericBaseException {
    public static final String FIELD_PARAM = "field";
    private static final String DEFAULT_MESSAGE = "Field not found";

    public FieldNotFoundException(String fieldName, Throwable cause) {
        super(ErrorCodeEnum.FIELD_NOT_FOUND, DEFAULT_MESSAGE, Map.of(FIELD_PARAM, fieldName), cause);
    }

    public FieldNotFoundException(String fieldName, String message, Throwable cause) {
        super(ErrorCodeEnum.FIELD_NOT_FOUND, message, Map.of(FIELD_PARAM, fieldName), cause);
    }

    public FieldNotFoundException(String fieldName) {
        super(ErrorCodeEnum.FIELD_NOT_FOUND, DEFAULT_MESSAGE, Map.of(FIELD_PARAM, fieldName));
    }

    public FieldNotFoundException(String fieldName, String msg) {
        super(ErrorCodeEnum.FIELD_NOT_FOUND, msg, Map.of(FIELD_PARAM, fieldName));
    }
}