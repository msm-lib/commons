package com.msm.core.dynamicquery.operator;

import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Condition;

public interface OperatorHandler {
    Condition handle(ObjectMetadata objectMetadata, FilterCondition condition);
}