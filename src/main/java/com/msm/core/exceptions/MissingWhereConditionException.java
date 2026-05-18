package com.msm.core.exceptions;

import lombok.ToString;

@ToString
public class MissingWhereConditionException extends GenericBaseException {
    public MissingWhereConditionException(String message) {
        super(ErrorCodeEnum.MISSING_WHERE_CONDITION, message);
    }

    public MissingWhereConditionException(String message, Throwable cause) {
        super(ErrorCodeEnum.MISSING_WHERE_CONDITION, message, cause);
    }
}