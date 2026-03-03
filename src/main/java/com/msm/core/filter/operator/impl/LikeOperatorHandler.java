package com.msm.core.filter.operator.impl;

import com.msm.core.filter.cache.EntityMetadataRegistry;
import com.msm.core.filter.domain.FieldMetadata;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.filter.domain.FilterOperator;
import com.msm.core.filter.operator.AbstractOperatorHandler;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringExpression;

import java.util.Objects;

public class LikeOperatorHandler extends AbstractOperatorHandler {

    @Override
    protected void validate(Path<?> path, Object value, FilterCondition condition) {
        FieldMetadata meta = EntityMetadataRegistry.get(path.getRoot().getType(), condition.getField());
        if (!meta.stringLike()) {
            throw typeError(FilterOperator.LIKE.name(), path, condition);
        }
    }

    @Override
    protected BooleanExpression doHandle(Path<?> path, Object value, FilterCondition c) {
        FieldMetadata meta = EntityMetadataRegistry.get(path.getRoot().getType(), c.getField());
        if (meta.isEnum()) {
            return handleEnum(path, value, meta);
        }

        if (meta.stringLike()) {
            StringExpression stringExp = Expressions.stringPath(path.getMetadata());
            if (Objects.nonNull(value)) {
                String typedValue = cast(path.getType(), value);
                return stringExp.containsIgnoreCase(typedValue);
            }
        }

        throw typeError(FilterOperator.LIKE.name(), path, c);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private BooleanExpression handleEnum(Path<?> path, Object value, FieldMetadata meta) {
        Object enumValue = Enum.valueOf((Class<Enum>) meta.javaType(), value.toString());
        SimpleExpression exp = Expressions.path(meta.javaType(), path.getMetadata());

        return exp.eq(enumValue);
    }

}