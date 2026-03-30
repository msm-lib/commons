package com.msm.core.exceptions;

public class DuplicateKeyException extends RuntimeException {
    public DuplicateKeyException() {}
    public DuplicateKeyException(String message) {
        super(message);
    }
    public Throwable fillInStackTrace() {
        return this;
    }
}
