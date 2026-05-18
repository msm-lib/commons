package com.msm.core.validate.domain;

import com.msm.core.exceptions.common.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageError {
    private ErrorCode code;
    private String label;
    private String attribute;
    private String message;
}