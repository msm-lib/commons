package com.msm.core.filter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class FilterCondition implements FilterObject {
    private String field;
    private FilterOperator operator;
    private Object value;
    private JoinType joinType = JoinType.INNER;

    public static FilterCondition create(String field, FilterOperator operator, Object value) {
        return FilterCondition.builder().field(field).operator(operator).value(value).build();
    }

    public static FilterCondition create(String field, FilterOperator operator, Object value, JoinType joinType) {
        return FilterCondition.builder().field(field).operator(operator).value(value).joinType(joinType).build();
    }
}
