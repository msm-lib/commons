package com.msm.core.filter.operator;

import com.msm.core.filter.cache.EntityMetadataFactory;
import com.msm.core.filter.domain.FieldMetadata;
import com.msm.core.filter.domain.FilterCondition;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.Expressions;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractComparableHandler extends AbstractOperatorHandler {

    @Override
    protected void validate(Path<?> path, Object value, FilterCondition condition) {
        FieldMetadata meta = EntityMetadataFactory.get(path.getRoot().getType(), condition.getField());
        if (!meta.isComparable()) {
            throw typeError("Comparable", path, condition);
        }
    }

    @Override
    protected final BooleanExpression doHandle(Path<?> path, Object value, FilterCondition c) {
        FieldMetadata meta = EntityMetadataFactory.get(path.getRoot().getType(), c.getField());
        ComparableExpression<?> exp = Expressions.comparablePath((Class) meta.getJavaType(), path.getMetadata());

        return doCompare(exp, value, c);
    }

    protected abstract BooleanExpression doCompare(ComparableExpression<?> exp, Object value, FilterCondition c);
}
