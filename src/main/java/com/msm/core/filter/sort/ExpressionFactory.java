package com.msm.core.filter.sort;

import com.msm.core.filter.cache.EntityMetadataRegistry;
import com.msm.core.filter.domain.FieldMetadata;
import com.msm.core.filter.domain.pageable.Sort;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;

public final class ExpressionFactory {

    private ExpressionFactory() {}

    public static Expression<?> toSortable(Path<?> path, Sort sort) {
        FieldMetadata meta = EntityMetadataRegistry.get(path.getRoot().getType(), sort.getAttribute());
        Class<?> type = meta.javaType();

        // String
        if (String.class.equals(type)) {
            return Expressions.stringPath(path.getMetadata());
        }

        // Number
        if (Number.class.isAssignableFrom(type)) {
            return Expressions.numberPath((Class) type, path.getMetadata());
        }

        // Enum
        if (type.isEnum()) {
            return Expressions.enumPath((Class<? extends Enum>) type, path.getMetadata());
        }

        // Date / Time
        if (Comparable.class.isAssignableFrom(type)) {
            return Expressions.comparablePath(
                    (Class<? extends Comparable>) type,
                    path.getMetadata()
            );
        }

        throw new IllegalArgumentException(
                "Field is not sortable: " + path
        );
    }
}
