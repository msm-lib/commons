package com.msm.core.filter.domain;

import lombok.Data;

import java.util.List;

@Data
public final class FilterGroup implements FilterObject {
    private LogicalOperator operator;
    private List<FilterObject> conditions;
}
