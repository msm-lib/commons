package com.msm.core.exceptions.common;

import com.msm.core.exceptions.ClientErrorTypeEnum;
import com.msm.core.exceptions.ErrorMessageTypeEnum;

public interface ServiceError {
    ErrorCode getCode();
    String getKey();
    String getMessage();

    default ClientErrorType getClient() {
        return ClientErrorTypeEnum.PORTAL_WEB;
    }

    default ErrorMessageType getType() {
        return ErrorMessageTypeEnum.MODEL_DETAILS;
    }
}
