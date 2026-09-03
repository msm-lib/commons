package com.msm.core.exceptions;

import com.msm.core.exceptions.common.ErrorMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessageTypeEnum implements ErrorMessageType {
    TOAST("TOAST"),
    MODEL_DETAILS("MODEL_DETAILS");


    private final String code;
}
