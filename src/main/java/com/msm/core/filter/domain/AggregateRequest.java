package com.msm.core.filter.domain;

import lombok.Data;

@Data
public class AggregateRequest {
    private AggregateType type;
    private String field;
    private String alias;
}
