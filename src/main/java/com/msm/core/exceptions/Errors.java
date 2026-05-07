package com.msm.core.exceptions;

public class Errors {
    public static UnsupportedException unsupported(String msg) {
        return new UnsupportedException(msg);
    }

    public static UnsupportedException unsupported(String msg, Throwable cause) {
        return new UnsupportedException(msg, cause);
    }

    public static InvalidFieldArgumentException invalid(String msg) {
        return new InvalidFieldArgumentException(msg);
    }

    public static InvalidFieldArgumentException invalid(String msg, Throwable cause) {
        return new InvalidFieldArgumentException(msg, cause);
    }

    public static ObjectCastException castException(String msg) {
        return new ObjectCastException(msg);
    }

    public static ObjectCastException castException(String msg, Throwable cause) {
        return new ObjectCastException(msg, cause);
    }

    public static FieldNotFoundException fieldNotFoundException(String msg) {
        return new FieldNotFoundException(msg);
    }

    public static FieldNotFoundException fieldNotFoundException(String msg, Throwable cause) {
        return new FieldNotFoundException(msg, cause);
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

    public static OptimisticLockingFailureException optimisticLockingFailureException(String msg, Throwable cause) {
        return new OptimisticLockingFailureException(msg, cause);
    }

    public static UnsafeDeleteException unsafeDeleteException(String msg) {
        return new UnsafeDeleteException(msg);
    }

    public static UnsafeDeleteException unsafeDeleteException(String msg, Throwable cause) {
        return new UnsafeDeleteException(msg, cause);
    }

//    public static RequestDataValidateException requestDataValidateException(String msg) {
//        return new RequestDataValidateException(msg);
//    }
//
//    public static RequestDataValidateException requestDataValidateException(String msg, Throwable cause) {
//        return new RequestDataValidateException(msg, cause);
//    }


    public static DuplicateKeyException duplicate(String msg) {
        return new DuplicateKeyException(msg);
    }

    public static HookMethodInvocationException hookError(String msg, Throwable ex) {
        return new HookMethodInvocationException(msg, ex);
    }
}
