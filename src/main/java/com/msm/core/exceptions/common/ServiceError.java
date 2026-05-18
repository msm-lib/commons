package com.msm.core.exceptions.common;

public interface ServiceError {
    ErrorCode getCode();
    String getKey();
    String getMessage();
}
