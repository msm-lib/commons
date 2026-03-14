package com.msm.core.filter.json;

import com.msm.core.filter.JsonExpressionFactory;
import com.msm.core.filter.domain.pageable.SortDirection;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringExpression;

import java.util.List;

public final class JsonOrderSpecifierFactory {

    private JsonOrderSpecifierFactory() {}

    public static OrderSpecifier<?> build(EntityPathBase<?> root, String jsonField, List<String> jsonPath, SortDirection dir) {
        StringExpression text = JsonbExpressions.text(root, jsonField, jsonPath);

        return dir == SortDirection.ASC
                ? text.asc()
                : text.desc();
    }
}
