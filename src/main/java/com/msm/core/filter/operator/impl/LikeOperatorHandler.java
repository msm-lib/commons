package com.msm.core.filter.operator.impl;

import com.msm.core.filter.EntityPathResolver;
import com.msm.core.filter.cache.EntityMetadataFactory;
import com.msm.core.filter.domain.FieldMetadata;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.filter.domain.FilterOperator;
import com.msm.core.filter.json.JsonbExpressions;
import com.msm.core.filter.operator.AbstractOperatorHandler;
import com.msm.core.filter.utils.ResolveUtils;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.*;

import java.util.List;
import java.util.Objects;

public class LikeOperatorHandler extends AbstractOperatorHandler {

    @Override
    protected void validate(Path<?> path, Object value, FilterCondition condition) {
        String metadataPath = ResolveUtils.resolveStringPath(path);
        FieldMetadata meta = EntityMetadataFactory.get(path.getRoot().getType(), metadataPath);
        if (meta.isJsonType()) {
            return;
        }
        if (!meta.isStringLike()) {
            throw typeError(FilterOperator.LIKE.name(), path, condition);
        }
    }

    @Override
    protected BooleanExpression doHandle(Path<?> path, Object value, FilterCondition c) {
        String metadataPath = ResolveUtils.resolveStringPath(path);
        FieldMetadata meta = EntityMetadataFactory.get(path.getRoot().getType(), metadataPath);

        if (meta.isEnum()) {
            return handleEnum(path, value, meta);
        }

        if (meta.isJsonType()) {
            List<String> parts = ResolveUtils.pathAsArray(c.getField());
            EntityPathBase<?> tEntityPathBase = EntityPathResolver.resolve(path.getRoot().getType());
            FieldMetadata metadata = EntityMetadataFactory.getFieldMetadata(path.getRoot().getType(), parts);

            parts.removeIf(metadataPath::contains);
            Expression<?> expression0 = JsonbExpressions.json(tEntityPathBase, metadataPath, parts, metadata.getJavaType());
            String typedValue = cast(String.class, value);
            return ((StringExpression) expression0).containsIgnoreCase(typedValue);
        }
        if (meta.isStringLike()) {
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
        Object enumValue = Enum.valueOf((Class<Enum>) meta.getJavaType(), value.toString());
        SimpleExpression exp = Expressions.path(meta.getJavaType(), path.getMetadata());

        return exp.eq(enumValue);
    }

}