package com.msm.core.validate.domain;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageError {
    private String code;
    private String label;
    private String attribute;
    private String message;
}