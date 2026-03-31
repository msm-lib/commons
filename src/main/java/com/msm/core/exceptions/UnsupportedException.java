package com.msm.core.exceptions;

public class UnsupportedException extends RuntimeException {
    public UnsupportedException() {}

    public UnsupportedException(String message) {
        super(message);
    }

    public UnsupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public Throwable fillInStackTrace() {
        return this;
    }
}
