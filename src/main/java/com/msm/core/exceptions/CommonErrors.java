package com.msm.core.exceptions;

public class CommonErrors {
    public static UnsupportedException unsupported(String fieldName, String msg) {
        return new UnsupportedException(fieldName, msg);
    }

    public static UnsupportedException unsupported(String fieldName, String msg, Throwable cause) {
        return new UnsupportedException(fieldName, msg, cause);
    }

    public static FieldValueInvalidException invalid(String fieldName, String msg) {
        return new FieldValueInvalidException(fieldName, msg);
    }

    public static FieldValueInvalidException invalid(String fieldName, String msg, Throwable cause) {
        return new FieldValueInvalidException(fieldName, msg, cause);
    }

    public static FieldValueRequiredException required(String fieldName, String msg) {
        return new FieldValueRequiredException(fieldName, msg);
    }

    public static FieldValueRequiredException required(String fieldName, String msg, Throwable cause) {
        return new FieldValueRequiredException(fieldName, msg, cause);
    }

    public static ObjectCastException castException(String fieldName, String msg) {
        return new ObjectCastException(fieldName, msg);
    }

    public static ObjectCastException castException(String fieldName, String msg, Throwable cause) {
        return new ObjectCastException(fieldName, msg, cause);
    }

    public static FieldNotFoundException fieldNotFoundException(String fieldName, String msg) {
        return new FieldNotFoundException(fieldName, msg);
    }

    public static FieldNotFoundException fieldNotFoundException(String fieldName, String msg, Throwable cause) {
        return new FieldNotFoundException(fieldName, msg, cause);
    }

    public static MissingWhereConditionException missingWhereConditionException(String msg) {
        return new MissingWhereConditionException(msg);
    }

    public static MissingWhereConditionException missingWhereConditionException(String msg, Throwable cause) {
        return new MissingWhereConditionException(msg, cause);
    }

    public static OptimisticLockingFailureException optimisticLockingFailureException(String msg) {
        return new OptimisticLockingFailureException(msg);
    }

    public static OptimisticLockingFailureException optimisticLockingFailureException(String fieldName, String msg) {
        return new OptimisticLockingFailureException(fieldName, msg);
    }

    public static OptimisticLockingFailureException optimisticLockingFailureException(String msg, Throwable cause) {
        return new OptimisticLockingFailureException(msg, cause);
    }

}
