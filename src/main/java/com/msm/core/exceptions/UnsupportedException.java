package com.msm.core.exceptions;

import java.util.Map;

public class UnsupportedException extends GenericBaseException {

    public static final String FIELD_PARAM = "resource";
    private static final String DEFAULT_MESSAGE = "Object is unsupported";

    public UnsupportedException(String fieldName, Throwable cause) {
        super(ErrorCodeEnum.UNSUPPORTED, DEFAULT_MESSAGE, Map.of(FIELD_PARAM, fieldName), cause);
    }

    public UnsupportedException(String fieldName, String message, Throwable cause) {
        super(ErrorCodeEnum.UNSUPPORTED, message, Map.of(FIELD_PARAM, fieldName), cause);
    }

    public UnsupportedException(String fieldName) {
        super(ErrorCodeEnum.UNSUPPORTED, DEFAULT_MESSAGE, Map.of(FIELD_PARAM, fieldName));
    }

    public UnsupportedException(String fieldName, String msg) {
        super(ErrorCodeEnum.UNSUPPORTED, msg, Map.of(FIELD_PARAM, fieldName));
    }
}