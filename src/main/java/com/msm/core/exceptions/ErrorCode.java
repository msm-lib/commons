package com.msm.core.exceptions;

public enum ErrorCode {
    UNSUPPORTED("UNSUPPORTED"),
    INVALID_ARGUMENT("INVALID_ARGUMENT"),
    INVALID_DATA_TYPE("INVALID_DATA_TYPE"),
    REQUIRE_INPUT_VALUE("REQUIRE_INPUT_VALUE"),
    HOOK_ERROR("HOOK_ERROR"),
    OBJECT_CAST_ERROR("OBJECT_CAST_ERROR"),
    FIELD_NOT_FOUND("OBJECT_CAST_ERROR"),
    DUPLICATE_KEY("DUPLICATE_KEY"),
    OPTIMISTIC_LOCKING_FAILURE("OPTIMISTIC_LOCKING_FAILURE"),
    MISSING_WHERE_CONDITION("MISSING_WHERE_CONDITION"),
    UNSAFE_DELETED("UNSAFE_DELETED"),
    ;

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
