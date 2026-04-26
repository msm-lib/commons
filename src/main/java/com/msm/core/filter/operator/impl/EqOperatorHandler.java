package com.msm.core.filter.operator.impl;

import com.msm.core.filter.EntityPathResolver;
import com.msm.core.filter.cache.EntityMetadataFactory;
import com.msm.core.filter.domain.FieldMetadata;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.filter.domain.FilterOperator;
import com.msm.core.filter.expressions.json.JsonbExpressions;
import com.msm.core.filter.operator.AbstractOperatorHandler;
import com.msm.core.filter.utils.ResolveUtils;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.SimpleExpression;

import java.util.List;
import java.util.Objects;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EqOperatorHandler extends AbstractOperatorHandler {

    @Override
    protected BooleanExpression doHandle(Path<?> path, Object value, FilterCondition c) {
        if (!(path instanceof SimpleExpression<?> exp)) {
            throw typeError(FilterOperator.EQUALS.name(), path, c);
        }

        if (Objects.nonNull(value)) {
            List<String> fullPath = ResolveUtils.extractPath(path);
            String metadataPath = ResolveUtils.resolveStringPath(path);
            FieldMetadata meta = EntityMetadataFactory.get(path.getRoot().getType(), metadataPath);
            if (meta.isJsonType()) {
                List<String> parts = ResolveUtils.pathAsArray(c.getField());
                FieldMetadata metadata = EntityMetadataFactory.getFieldMetadata(path.getRoot().getType(), parts);
                EntityPathBase<?> tEntityPathBase = EntityPathResolver.resolve(path.getRoot().getType());
                parts.removeIf(fullPath::contains);

                Expression<?> expression0 = JsonbExpressions.json(tEntityPathBase, metadataPath, parts, metadata.getJavaType());
                return ((SimpleExpression) expression0).eq(value);
            } else {
                Object typedValue = cast(meta.getJavaType(), value);
                return ((SimpleExpression) exp).eq(typedValue);
            }
        }

        return exp.isNull();
    }
}