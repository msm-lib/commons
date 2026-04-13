package com.msm.core.dynamicquery;


import com.msm.core.dynamicquery.operator.OperatorFactory;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.filter.domain.FilterGroup;
import com.msm.core.filter.domain.FilterObject;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;
import java.util.List;

public class FilterBuilder {

    public static Condition build(FilterObject filter, Table<?> table) {
        if (filter == null) {
            return DSL.noCondition();
        }

        if (filter instanceof FilterCondition fc) {
            return buildCondition(table, fc);
        }

        if (filter instanceof FilterGroup fg) {
            return buildGroup(fg, table);
        }

        throw new IllegalArgumentException("Unknown filter type");
    }

    private static Condition buildGroup(FilterGroup group, Table<?> table) {
        List<Condition> conditions = group
                .getConditions()
                .stream()
                .map(f -> build(f, table))
                .toList();

        if (conditions.isEmpty()) {
            return DSL.noCondition();
        }

        return switch (group.getOperator()) {
            case AND -> DSL.and(conditions);
            case OR -> DSL.or(conditions);
        };
    }

    private static Condition buildCondition(Table<?> table, FilterCondition c) {
        Field<?> path = FieldResolver.resolve(table, c.getField());
        return OperatorFactory.get(c.getOperator()).handle(path, c);
    }
}
