package com.msm.core.exceptions;

public class UnsafeDeleteException extends GenericBaseException {
    public UnsafeDeleteException(String message) {
        super(ErrorCode.UNSAFE_DELETED.getCode(), message);
    }

    public UnsafeDeleteException(String message, Throwable cause) {
        super(ErrorCode.UNSAFE_DELETED.getCode(), message, cause);
    }
}