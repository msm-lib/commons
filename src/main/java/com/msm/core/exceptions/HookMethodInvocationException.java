package com.msm.core.exceptions;

public class HookMethodInvocationException extends RuntimeException {
    public HookMethodInvocationException() {}

    public HookMethodInvocationException(String message) {
        super(message);
    }

    public HookMethodInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public Throwable fillInStackTrace() {
        return this;
    }
}
