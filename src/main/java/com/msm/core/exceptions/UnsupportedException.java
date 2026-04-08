package com.msm.core.exceptions;

public class UnsupportedException extends GenericBaseException {

    public UnsupportedException(String message) {
        super(ErrorCode.UNSUPPORTED.getCode(), message);
    }

    public UnsupportedException(String message, Throwable cause) {
        super(ErrorCode.UNSUPPORTED.getCode(), message, cause);
    }
}