package com.msm.core.dynamicquery;

import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;
import com.msm.core.dynamicquery.mapping.JavaTypeMappingFactory;
import com.msm.core.exceptions.CommonErrors;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FieldResolver {

    public static String resolve(String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        return parts[0];
    }

    public static Field<?> resolve(ObjectMetadata objectMetadata, String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        Attribute attribute = objectMetadata.getAttributeByName(parts[0]);

        // ===== Support lookup reference =====
        if (attribute == null && parts[0].endsWith(Constants.REFERENCE_SUFFIX)) {
            Attribute customValues = objectMetadata.getAttributeByName(Constants.CUSTOM_VALUE_NAME);
            if (customValues != null && customValues.isJsonField()) {
                return buildJsonField(customValues, Utils.ARRAYS.concat(
                        new String[]{Constants.CUSTOM_VALUE_NAME},
                        parts
                ));
            }
        }

        if(attribute == null) {
            throw CommonErrors.fieldNotFoundException(fieldPath, fieldPath + " not found");
        }
        Field<?> base = attribute.getField();
        if (parts.length == 1) {
            return base;
        }
        // JSON field
        if (attribute.isJsonField()) {
            return buildJsonField(attribute, parts);
        }

        // Unsupported nested relation
        throw CommonErrors.invalid(base.getName(), "Invalid nested field: " + base.getName());
    }

//    private static boolean isJsonField(Field<?> field) {
//        if (field == null) {
//            return false;
//        }
//        Class<?> type = field.getType();
//        return type.getName().equalsIgnoreCase("org.jooq.JSON")
//                || type.getName().equalsIgnoreCase("org.jooq.JSONB");
//    }


    private static Field<?> buildJsonField(Attribute attribute, String[] parts) {
        // parts[0] = name of column JSON
        // parts[1..n] = path of JSON
        Field<?> result = attribute.getField();

        for (int i = 1; i < parts.length; i++) {
            boolean isLast = (i == parts.length - 1);
            if (isLast) {
                result = DSL.field(
                        "({0} ->> {1})",
                        Object.class,
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

        return result.convertFrom(JavaTypeMappingFactory.DYNAMIC_JSON_CONVERTER::from);
    }

    public static List<String> pathAsArray(String path) {
        return Arrays.stream(path.split("\\.")).collect(Collectors.toList());
    }

    public static String pathAsFieldName(String path) {
        return path.split("\\.")[0];
    }

}

