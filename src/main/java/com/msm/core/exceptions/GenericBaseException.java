package com.msm.core.exceptions;

public abstract class GenericBaseException extends RuntimeException {

    private final String code;

    protected GenericBaseException(String code, String message) {
        super(message);
        this.code = code;
    }

    protected GenericBaseException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}