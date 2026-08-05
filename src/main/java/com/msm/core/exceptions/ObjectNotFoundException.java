package com.msm.core.exceptions;

import com.msm.core.commons.Utils;

import java.util.Map;

public class ObjectNotFoundException extends GenericBaseException {
    public static final String FIELD_PARAM = "resource";
    private static final String DEFAULT_MESSAGE = "Object[{0}] not found";

    public ObjectNotFoundException(String objectName, Throwable cause) {
        super(ErrorCodeEnum.OBJECT_NOT_FOUND, Utils.STR.format(DEFAULT_MESSAGE, objectName), Map.of(FIELD_PARAM, objectName), cause);
    }

    public ObjectNotFoundException(String objectName, String message, Throwable cause) {
        super(ErrorCodeEnum.OBJECT_NOT_FOUND, message, Map.of(FIELD_PARAM, objectName), cause);
    }

    public ObjectNotFoundException(String objectName) {
        super(ErrorCodeEnum.OBJECT_NOT_FOUND, Utils.STR.format(DEFAULT_MESSAGE, objectName), Map.of(FIELD_PARAM, objectName));
    }

    public ObjectNotFoundException(String objectName, String msg) {
        super(ErrorCodeEnum.OBJECT_NOT_FOUND, msg, Map.of(FIELD_PARAM, objectName));
    }
}
