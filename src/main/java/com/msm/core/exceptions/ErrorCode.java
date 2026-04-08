package com.msm.core.exceptions;

public enum ErrorCode {
    UNSUPPORTED("UNSUPPORTED"),
    INVALID_ARGUMENT("INVALID_ARGUMENT"),
    HOOK_ERROR("HOOK_ERROR"),
    DUPLICATE_KEY("DUPLICATE_KEY");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
