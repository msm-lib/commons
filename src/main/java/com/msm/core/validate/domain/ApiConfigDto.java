package com.msm.core.validate.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiConfigDto {
    private String apiCode;
    private String endpoint;
    private String method;
}