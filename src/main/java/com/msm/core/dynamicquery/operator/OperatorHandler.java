package com.msm.core.dynamicquery.operator;

import com.msm.core.filter.domain.FilterCondition;
import org.jooq.Condition;
import org.jooq.Field;

public interface OperatorHandler {
    Condition handle(Field<?> field, FilterCondition condition);
}