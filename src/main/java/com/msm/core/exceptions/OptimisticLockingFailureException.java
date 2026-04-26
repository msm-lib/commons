package com.msm.core.exceptions;

public class OptimisticLockingFailureException extends GenericBaseException {
    public OptimisticLockingFailureException(String message) {
        super(ErrorCode.OPTIMISTIC_LOCKING_FAILURE.getCode(), message);
    }

    public OptimisticLockingFailureException(String message, Throwable cause) {
        super(ErrorCode.OPTIMISTIC_LOCKING_FAILURE.getCode(), message, cause);
    }
}