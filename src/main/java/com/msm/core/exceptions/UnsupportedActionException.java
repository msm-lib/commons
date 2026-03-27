package com.msm.core.exceptions;

public class UnsupportedActionException extends RuntimeException {
    public UnsupportedActionException() {}
    public UnsupportedActionException(String message) {
        super(message);
    }
    public Throwable fillInStackTrace() {
        return this;
    }
}
