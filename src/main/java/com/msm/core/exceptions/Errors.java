package com.msm.core.exceptions;

public class Errors {
    public static UnsupportedException unsupported(String msg) {
        return new UnsupportedException(msg);
    }

    public static InvalidFieldArgumentException invalid(String msg) {
        return new InvalidFieldArgumentException(msg);
    }

    public static DuplicateKeyException duplicate(String msg) {
        return new DuplicateKeyException(msg);
    }

    public static HookMethodInvocationException hookError(String msg, Throwable ex) {
        return new HookMethodInvocationException(msg, ex);
    }
}
