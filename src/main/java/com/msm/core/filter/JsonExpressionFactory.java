package com.msm.core.filter;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;

import java.util.ArrayList;
import java.util.List;

public final class JsonExpressionFactory {

    private JsonExpressionFactory() {}

    public static StringExpression jsonText(
            EntityPathBase<?> root,
            String jsonField,
            List<String> path) {
        List<Expression<?>> args = new ArrayList<>();

        // jsonb column
        args.add(
                Expressions.path(
                        Object.class,
                        root,
                        jsonField
                )
        );

        // json path
        for (String p : path) {
            args.add(Expressions.constant(p));
        }

        return Expressions.stringTemplate(
                "jsonb_extract_path_text({0}, {1})",
                args.toArray()
        );
    }

}
