package com.msm.core.dynamicquery.query;


import com.msm.core.dynamicquery.operator.OperatorFactory;
import com.msm.core.exceptions.CommonErrors;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.filter.domain.FilterGroup;
import com.msm.core.filter.domain.FilterObject;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Condition;
import org.jooq.impl.DSL;

import java.util.List;

public class FilterBuilder {

    public static Condition build(FilterObject filter, ObjectMetadata meta) {
        if (filter == null) {
            return DSL.noCondition();
        }

        if (filter instanceof FilterCondition fc) {
            return buildCondition(fc, meta);
        }

        if (filter instanceof FilterGroup fg) {
            return buildGroup(fg, meta);
        }

        throw CommonErrors.invalid("Unknown", "Unknown filter type");
    }

    private static Condition buildGroup(FilterGroup group, ObjectMetadata meta) {
        List<Condition> conditions = group
                .getConditions()
                .stream()
                .map(f -> build(f, meta))
                .toList();

        if (conditions.isEmpty()) {
            return DSL.noCondition();
        }

        return switch (group.getOperator()) {
            case AND -> DSL.and(conditions);
            case OR -> DSL.or(conditions);
        };
    }

    private static Condition buildCondition(FilterCondition condition, ObjectMetadata meta) {
        return OperatorFactory.get(condition.getOperator()).handle(meta, condition);
    }
}
