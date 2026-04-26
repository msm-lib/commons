package com.msm.core.exceptions;

public class InvalidFieldArgumentException extends GenericBaseException {

    public InvalidFieldArgumentException(String message) {
        super(ErrorCode.INVALID_ARGUMENT.getCode(), message);
    }

    public InvalidFieldArgumentException(String message, Throwable cause) {
        super(ErrorCode.INVALID_ARGUMENT.getCode(), message, cause);
    }
}