package com.msm.core.filter.operator.impl;

import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.filter.operator.AbstractOperatorHandler;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;

public class JsonEqOperatorHandler extends AbstractOperatorHandler {
    @Override
    protected BooleanExpression doHandle(Path<?> path, Object value, FilterCondition condition) {
        return null;
    }

//    @Override
//    protected BooleanExpression doHandle(Path<?> path, Object value, FilterCondition c) {
//        FieldMetadata meta = EntityMetadataRegistry.get(
//                path.getRoot().getType(),
//                c.getField()
//        );
//
////        if (!meta.jsonLike()) {
////            throw typeError(FilterOperator.EQ.name(), path, c);
////        }
//
//        // CASE 1: EQ object → @>
//        if (value instanceof Map<?, ?> || value instanceof Collection<?>) {
//            return jsonContains(path, value);
//        }
//
//        // CASE 2: EQ scalar → #>>
//        return jsonScalarEq(path, value, c, meta);
//    }
//
//    // =====================================
//    // payload @> '{"a":"b"}'
//    // =====================================
//    private BooleanExpression jsonContains(
//            Path<?> path,
//            Object value
//    ) {
//        String json =
//                ObjectMappers.DEFAULT
//                        .writeValueAsString(value);
//
//        return Expressions.booleanTemplate(
//                "{0} @> {1}::jsonb",
//                path,
//                Expressions.constant(json)
//        );
//    }
//
//    // =====================================
//    // payload #>> '{a,b}' = 'value'
//    // =====================================
//    private BooleanExpression jsonScalarEq(
//            Path<?> path,
//            Object value,
//            FilterCondition c,
//            FieldMetadata meta
//    ) {
//        JsonPath jsonPath =
//                JsonPathUtils.parse(
//                        c.getField(),
//                        meta
//                );
//
//        StringExpression jsonText =
//                Expressions.stringTemplate(
//                        "{0} #>> {1}",
//                        path,
//                        Expressions.constant(
//                                "{" + String.join(",", jsonPath.path()) + "}"
//                        )
//                );
//
//        return jsonText.eq(value.toString());
//    }
}
