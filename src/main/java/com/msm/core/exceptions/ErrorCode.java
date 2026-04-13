package com.msm.core.exceptions;

public enum ErrorCode {
    UNSUPPORTED("UNSUPPORTED"),
    INVALID_ARGUMENT("INVALID_ARGUMENT"),
    INVALID_DATA_TYPE("INVALID_DATA_TYPE"),
    REQUIRE_INPUT_VALUE("REQUIRE_INPUT_VALUE"),
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
