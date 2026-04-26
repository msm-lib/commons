package com.msm.core.exceptions;

public class MissingWhereConditionException extends GenericBaseException {
    public MissingWhereConditionException(String message) {
        super(ErrorCode.MISSING_WHERE_CONDITION.getCode(), message);
    }

    public MissingWhereConditionException(String message, Throwable cause) {
        super(ErrorCode.MISSING_WHERE_CONDITION.getCode(), message, cause);
    }
}