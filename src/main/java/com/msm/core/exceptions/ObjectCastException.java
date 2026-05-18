package com.msm.core.exceptions;

import java.util.Map;

public class ObjectCastException extends GenericBaseException {
    public static final String FIELD_PARAM = "objectName";
    private static final String DEFAULT_MESSAGE = "Object value invalid";

    public ObjectCastException(String fieldName, Throwable cause) {
        super(ErrorCodeEnum.OBJECT_CAST_ERROR, DEFAULT_MESSAGE, Map.of(FIELD_PARAM, fieldName), cause);
    }

    public ObjectCastException(String fieldName, String message, Throwable cause) {
        super(ErrorCodeEnum.OBJECT_CAST_ERROR, message, Map.of(FIELD_PARAM, fieldName), cause);
    }

    public ObjectCastException(String fieldName) {
        super(ErrorCodeEnum.OBJECT_CAST_ERROR, DEFAULT_MESSAGE, Map.of(FIELD_PARAM, fieldName));
    }

    public ObjectCastException(String fieldName, String msg) {
        super(ErrorCodeEnum.OBJECT_CAST_ERROR, msg, Map.of(FIELD_PARAM, fieldName));
    }
}