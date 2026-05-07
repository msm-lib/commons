package com.msm.core.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.msm.core.commons.Utils;
import com.msm.core.validate.domain.MessageError;

import java.util.List;

public class RequestDataValidateException extends GenericBaseException {

    public RequestDataValidateException(String message) {
        super(ErrorCode.REQUEST_DATA_VALIDATE.getCode(), message);
    }

    public RequestDataValidateException(String message, Throwable cause) {
        super(ErrorCode.REQUEST_DATA_VALIDATE.getCode(), message, cause);
    }

    private String getMessageAsString(List<MessageError> message) {
        try {
            return Utils.O.toJsonString(message);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }
}