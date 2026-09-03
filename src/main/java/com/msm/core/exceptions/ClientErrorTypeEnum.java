package com.msm.core.exceptions;

import com.msm.core.exceptions.common.ClientErrorType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClientErrorTypeEnum implements ClientErrorType {
    PORTAL_WEB("PORTAL_WEB"),
    MOBILE("MOBILE");


    private final String code;
}
