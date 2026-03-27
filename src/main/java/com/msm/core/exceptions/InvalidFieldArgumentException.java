package com.msm.core.exceptions;

public class InvalidFieldArgumentException extends RuntimeException{
    public InvalidFieldArgumentException() {}
    public InvalidFieldArgumentException(String message) {
        super(message);
    }
    public Throwable fillInStackTrace() {
        return this;
    }
}
