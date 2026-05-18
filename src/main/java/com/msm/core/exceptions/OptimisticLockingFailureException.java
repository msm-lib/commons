package com.msm.core.exceptions;

import java.util.Map;

public class OptimisticLockingFailureException extends GenericBaseException {

    public static final String FIELD_PARAM = "resource";
    private static final String DEFAULT_MESSAGE = "Version conflict or record not found";

    public OptimisticLockingFailureException(String fieldName, Throwable cause) {
        super(ErrorCodeEnum.OPTIMISTIC_LOCKING_FAILURE, DEFAULT_MESSAGE, Map.of(FIELD_PARAM, fieldName), cause);
    }

    public OptimisticLockingFailureException(String fieldName, String message, Throwable cause) {
        super(ErrorCodeEnum.OPTIMISTIC_LOCKING_FAILURE, message, Map.of(FIELD_PARAM, fieldName), cause);
    }

    public OptimisticLockingFailureException(String fieldName) {
        super(ErrorCodeEnum.OPTIMISTIC_LOCKING_FAILURE, DEFAULT_MESSAGE, Map.of(FIELD_PARAM, fieldName));
    }

    public OptimisticLockingFailureException(String fieldName, String msg) {
        super(ErrorCodeEnum.OPTIMISTIC_LOCKING_FAILURE, msg, Map.of(FIELD_PARAM, fieldName));
    }
}