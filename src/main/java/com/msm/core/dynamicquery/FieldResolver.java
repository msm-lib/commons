package com.msm.core.dynamicquery;

import com.msm.core.commons.Utils;
import com.msm.core.exceptions.Errors;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

public class FieldResolver {

    public static Field<?> resolve(Table<?> table, String fieldPath) {
        String[] parts = fieldPath.split("\\.");

        String columnField = Utils.STR.toSnakeCase(parts[0]);
        Field<?> base = table.field(columnField);

        if (parts.length == 1) {
            return base;
        }

        // JSON field
        if (isJsonField(base)) {
            return buildJsonField(parts);
        }

        // Unsupported nested relation
        throw Errors.invalid("Invalid nested field: " + base.getName());
    }

    private static boolean isJsonField(Field<?> field) {
        if (field == null) {
            return false;
        }
        Class<?> type = field.getType();
        return type.getName().equalsIgnoreCase("org.jooq.JSON")
                || type.getName().equalsIgnoreCase("org.jooq.JSONB");
    }

    private static Field<?> buildJsonField(String[] parts) {
        // parts[0] = name of column JSON
        // parts[1..n] = path of JSON
        String columnField = Utils.STR.toSnakeCase(parts[0]);
        Field<?> result = DSL.field(DSL.name(columnField));

        for (int i = 1; i < parts.length; i++) {
            boolean isLast = (i == parts.length - 1);
            if (isLast) {
                result = DSL.field(
                        "({0} ->> {1})",
                        String.class,
                        result,
                        DSL.inline(parts[i])
                );
            } else {
                result = DSL.field(
                        "({0} -> {1})",
                        Object.class,
                        result,
                        DSL.inline(parts[i])
                );
            }
        }

        return result;
    }

//    private Field<?> resolveJoinField(Table<?> root, String[] parts) {
//        Table<?> currentTable = root;
//        Field<?> field = null;
//
//        for (int i = 0; i < parts.length; i++) {
//            String part = parts[i];
//
//            // Nếu là phần cuối → lấy field
//            if (i == parts.length - 1) {
//                field = currentTable.field(part);
//                break;
//            }
//
//            // Nếu không → join sang table khác
//            currentTable = resolveRelation(currentTable, part);
//        }
//
//        return field;
//    }
//
//    private Table<?> resolveRelation(Table<?> table, String relationName) {
//        // Ví dụ hardcode (thực tế nên dùng metadata hoặc jOOQ generated schema)
//
//        if (table.getName().equals("orders") && relationName.equals("user")) {
//            return USERS;
//        }
//
//        if (table.getName().equals("users") && relationName.equals("address")) {
//            return ADDRESSES;
//        }
//
//        throw new IllegalArgumentException(
//                "Unknown relation: " + table.getName() + "." + relationName
//        );
//    }


}

