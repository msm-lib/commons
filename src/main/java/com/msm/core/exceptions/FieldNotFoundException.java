package com.msm.core.exceptions;

public class FieldNotFoundException extends GenericBaseException {

    public FieldNotFoundException(String message) {
        super(ErrorCode.FIELD_NOT_FOUND.getCode(), message);
    }

    public FieldNotFoundException(String message, Throwable cause) {
        super(ErrorCode.FIELD_NOT_FOUND.getCode(), message, cause);
    }
}