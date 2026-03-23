package com.msm.core.filter.operator.impl;

import com.msm.core.filter.EntityPathResolver;
import com.msm.core.filter.cache.EntityMetadataFactory;
import com.msm.core.filter.cache.PathCache;
import com.msm.core.filter.domain.FieldMetadata;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.filter.domain.FilterOperator;
import com.msm.core.filter.json.JsonbExpressions;
import com.msm.core.filter.operator.AbstractOperatorHandler;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.*;

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
            FieldMetadata meta = EntityMetadataFactory.get(path.getRoot().getType(), c.getField());
            Object typedValue = cast(meta.javaType(), value);
            List<String> parts = PathCache.pathAsArray(c.getField());
            if (meta.jsonType()) {
                EntityPathBase<?> tEntityPathBase = EntityPathResolver.resolve(path.getRoot().getType());
                FieldMetadata metadata = EntityMetadataFactory.getFieldMetadata(path.getRoot().getType(), parts);
                Expression<?> expression0 = JsonbExpressions.json(tEntityPathBase, meta.field(), parts.subList(1, parts.size()), metadata.javaType());
                return ((SimpleExpression) expression0).eq(typedValue);
            } else {
                return ((SimpleExpression) exp).eq(typedValue);
            }
        }

        return exp.isNull();
    }

//    private FieldMetadata getFieldMetadata(Path<?> path, List<String> parts) {
//
//        FieldMetadata fieldMetadata = EntityMetadataRegistry.get(path.getRoot().getType(), parts.getFirst());
//        if(parts.size() == 1) {
//            return fieldMetadata;
//        }
//        Class<?> entityClass = fieldMetadata.javaType();
//        for (int i = 1; i <= parts.size() - 1; i++) {
//            FieldMetadata meta = EntityMetadataRegistry.get(entityClass, parts.get(i));
//            if(Objects.nonNull(meta)) {
//                fieldMetadata = meta;
//                entityClass = meta.javaType();
//            }
//        }
//        return fieldMetadata;
//    }
}