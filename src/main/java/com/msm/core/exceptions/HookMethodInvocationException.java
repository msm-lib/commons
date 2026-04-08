package com.msm.core.exceptions;

public class HookMethodInvocationException extends GenericBaseException {

    public HookMethodInvocationException(String message) {
        super(ErrorCode.HOOK_ERROR.getCode(), message);
    }

    public HookMethodInvocationException(String message, Throwable cause) {
        super(ErrorCode.HOOK_ERROR.getCode(), message, cause);
    }
}