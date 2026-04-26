package com.msm.core.filter.expressions.json;

import com.msm.core.filter.DataTypeUtils;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class JsonbExpressions {

    private JsonbExpressions(){}

    private static Expression<?> jsonColumn(EntityPathBase<?> root, String jsonColumn) {

        return Expressions.path(Object.class, root, jsonColumn);
    }

    private static String buildFunctionTemplate(int pathSize) {

        StringBuilder template = new StringBuilder("function('jsonb_extract_path_text',{0}");

        for (int i = 0; i < pathSize; i++) {
            template.append(",{").append(i + 1).append("}");
        }

        template.append(")");

        return template.toString();
    }

    private static Expression<?>[] buildArgs(
            EntityPathBase<?> root,
            String jsonColumn,
            List<String> path) {

        List<Expression<?>> args = new ArrayList<>();

        args.add(jsonColumn(root, jsonColumn));

        for (String p : path) {
            args.add(Expressions.constant(p));
        }

        return args.toArray(new Expression[0]);
    }

    // ---------- TEXT ----------

    public static StringExpression text(
            EntityPathBase<?> root,
            String jsonColumn,
            List<String> path) {

        return Expressions.stringTemplate(
                buildFunctionTemplate(path.size()),
                buildArgs(root, jsonColumn, path)
        );
    }

    // ---------- JSON ----------

    public static SimpleExpression<Object> jsonObject(
            EntityPathBase<?> root,
            String jsonColumn,
            List<String> path) {

        List<Expression<?>> args = new ArrayList<>();

        args.add(Expressions.path(Object.class, root, jsonColumn));

        for(String p : path){
            args.add(Expressions.constant(p));
        }

        String template = "function('jsonb_extract_path',{0}"
                        + ",{1}".repeat(path.size())
                        + ")";

        return Expressions.simpleTemplate(
                Object.class,
                template,
                args.toArray()
        );
    }

    // ---------- NUMBER ----------

    public static NumberExpression<Integer> intValue(
            EntityPathBase<?> root,
            String jsonColumn,
            List<String> path) {

        String template = "cast(" + buildFunctionTemplate(path.size()) + " as integer)";

        return Expressions.numberTemplate(
                Integer.class,
                template,
                buildArgs(root, jsonColumn, path)
        );
    }

    public static NumberExpression<Integer> longValue(
            EntityPathBase<?> root,
            String jsonColumn,
            List<String> path) {

        String template = "cast(" + buildFunctionTemplate(path.size()) + " as bigint)";

        return Expressions.numberTemplate(
                Integer.class,
                template,
                buildArgs(root, jsonColumn, path)
        );
    }

    public static NumberExpression<Double> doubleValue(
            EntityPathBase<?> root,
            String jsonColumn,
            List<String> path) {

        String template =
                "cast(" + buildFunctionTemplate(path.size()) + " as double)";

        return Expressions.numberTemplate(
                Double.class,
                template,
                buildArgs(root, jsonColumn, path)
        );
    }

    // ---------- BOOLEAN ----------

    public static BooleanExpression boolValue(
            EntityPathBase<?> root,
            String jsonColumn,
            List<String> path) {

        String template =
                "cast(" + buildFunctionTemplate(path.size()) + " as boolean)";

        return Expressions.booleanTemplate(
                template,
                buildArgs(root, jsonColumn, path)
        );
    }

    // ---------- JSON CONTAINS ----------

    public static BooleanExpression contains(
            EntityPathBase<?> root,
            String jsonColumn,
            String json) {

        return Expressions.booleanTemplate(
                "function('jsonb_contains',{0},{1})",
                jsonColumn(root, jsonColumn),
                Expressions.constant(json)
        );
    }

    // ---------- KEY EXISTS ----------

    public static BooleanExpression hasKey(
            EntityPathBase<?> root,
            String jsonColumn,
            String key) {

        return Expressions.booleanTemplate(
                "function('jsonb_exists',{0},{1})",
                jsonColumn(root, jsonColumn),
                Expressions.constant(key)
        );
    }

    public static Expression<?> get(EntityPathBase<?> root, String jsonColumn, String key) {

        return Expressions.booleanTemplate(
                "function('jsonb_exists',{0},{1})",
                jsonColumn(root, jsonColumn),
                Expressions.constant(key)
        );
    }

    public static <T> Expression<T> json(
            EntityPathBase<?> root,
            String jsonColumn,
            List<String> path,
            Class<T> type) {

        type = (Class<T>) DataTypeUtils.normalize(type);

        if(type == String.class)
            return (Expression<T>) text(root, jsonColumn, path);

        if(type == Integer.class){
            return (Expression<T>) intValue(root, jsonColumn, path);
        }

        if(type == Long.class){
            return (Expression<T>) longValue(root, jsonColumn, path);
        }

        if(type == Double.class){
            return (Expression<T>) doubleValue(root, jsonColumn, path);
        }

        if(type == Boolean.class){
            return (Expression<T>) boolValue(root, jsonColumn, path);
        }

        return (Expression<T>) text(root, jsonColumn, path);
    }

    public static <T> Expression<T> json(
            PathBuilder<?> root,
            String field,
            Class<T> type) {

        String[] parts = field.split("\\.");

        String column = parts[0];

        List<String> path = Arrays.asList(parts).subList(1, parts.length);

        List<Expression<?>> args = new ArrayList<>();

        args.add(root.get(column));

        path.forEach(p ->
                args.add(Expressions.constant(p))
        );

//        String template =
//                "function('jsonb_extract_path_text',{0}"
//                        + ",{1}".repeat(path.size())
//                        + ")";
        String template = buildFunctionTemplate(path.size());

        Expression<String> textExpr = Expressions.stringTemplate(
                buildFunctionTemplate(path.size()),
                       buildArgs(root, column, path)
                );

        return cast(textExpr, type);
    }

    @SuppressWarnings("unchecked")
    private static <T> Expression<T> cast(
            Expression<String> expr,
            Class<T> type) {
        type = (Class<T>) DataTypeUtils.normalize(type);

        if(type == String.class)
            return (Expression<T>) expr;

        if(type == Integer.class){

            return (Expression<T>) Expressions.numberTemplate(
                    Integer.class,
                    "cast({0} as integer)",
                    expr
            );
        }

        if(type == Long.class){

            return (Expression<T>) Expressions.numberTemplate(
                    Long.class,
                    "cast({0} as bigint)",
                    expr
            );
        }

        if(type == Double.class){

            return (Expression<T>) Expressions.numberTemplate(
                    Double.class,
                    "cast({0} as double)",
                    expr
            );
        }

        if(type == Boolean.class){

            return (Expression<T>) Expressions.booleanTemplate(
                    "cast({0} as boolean)",
                    expr
            );
        }

        return (Expression<T>) expr;
    }

}