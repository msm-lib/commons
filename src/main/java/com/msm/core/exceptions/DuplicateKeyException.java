package com.msm.core.exceptions;

public class DuplicateKeyException extends GenericBaseException {

    public DuplicateKeyException(String message) {
        super(ErrorCode.DUPLICATE_KEY.getCode(), message);
    }
}