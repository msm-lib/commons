package com.msm.core.filter.domain;

import lombok.Data;

@Data
public class DateRange {
    private String from;
    private String to;
    private String timezone;
}
