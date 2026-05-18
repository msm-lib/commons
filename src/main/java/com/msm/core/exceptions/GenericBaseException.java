package com.msm.core.exceptions;

//public abstract class GenericBaseException extends RuntimeException {
//
//    private final String code;
//
//    protected GenericBaseException(String code, String message) {
//        super(message);
//        this.code = code;
//    }
//
//    protected GenericBaseException(String code, String message, Throwable cause) {
//        super(message, cause);
//        this.code = code;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    @Override
//    public Throwable fillInStackTrace() {
//        return this;
//    }
//}

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
