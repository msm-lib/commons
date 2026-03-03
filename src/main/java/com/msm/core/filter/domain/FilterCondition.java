package com.msm.core.filter.domain;

import lombok.Data;

@Data
public final class FilterCondition implements FilterObject {

    private String field;
    private FilterOperator operator;
    private Object value;
    private JoinType joinType = JoinType.INNER;
}
