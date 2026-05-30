package com.msm.core.exceptions;

import com.msm.core.exceptions.common.ErrorCode;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
public abstract class GenericBaseException extends RuntimeException {

    private final ErrorCode code;
    private final Map<String, Object> params;

    protected GenericBaseException(ErrorCode code, String message) {
        this(code, message, Collections.emptyMap(), null);
    }

    protected GenericBaseException(ErrorCode code, String message, Throwable cause) {
        this(code, message, Collections.emptyMap(), cause);
    }

    protected GenericBaseException(ErrorCode code, String message, Map<String, Object> params) {
        this(code, message, params, null);
    }

    protected GenericBaseException(ErrorCode code, String message, Map<String, Object> params, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.params = params;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
