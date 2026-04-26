package com.msm.core.exceptions;

public class ObjectCastException extends GenericBaseException {

    public ObjectCastException(String message) {
        super(ErrorCode.OBJECT_CAST_ERROR.getCode(), message);
    }

    public ObjectCastException(String message, Throwable cause) {
        super(ErrorCode.OBJECT_CAST_ERROR.getCode(), message, cause);
    }
}