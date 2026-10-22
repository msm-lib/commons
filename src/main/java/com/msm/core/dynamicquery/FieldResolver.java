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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FieldResolver {

    public static String resolve(String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        return parts[0];
    }

    /**
     * Target of a filter condition.
     *
     * @param attribute the attribute the condition is typed by (custom_values for a reference path)
     * @param field     its SQL field: the column, or the JSON path ({@code ->>} returns text)
     * @param json      the attribute is a JSON column (a JSON path or the whole JSON column)
     * @param path      the field is a path inside the JSON column, not the column itself
     */
    public record ResolvedField(Attribute attribute, Field<?> field, boolean json, boolean path) {
    }

    // A JSON key of a filter path: no quote, dot or operator character
    private static final Pattern PATH_SEGMENT = Pattern.compile("[A-Za-z0-9_]+");
    private static final int MAX_PATH_DEPTH = 5;

    /**
     * Resolves the field of a filter condition:
     * <ul>
     *   <li>{@code code}: a column ({@code code.x} too, filtered on the column as before);</li>
     *   <li>{@code customValues.xIdReference.name}: a path inside a JSON column;</li>
     *   <li>{@code xIdReference.name}: a path inside custom_values, when the object declares the reference
     *   {@code xIdReference} (attributeRef fieldName). Undeclared keys are not found, so a filter cannot probe any key
     *   of custom_values.</li>
     * </ul>
     */
    public static ResolvedField resolveTarget(ObjectMetadata objectMetadata, String fieldPath) {
        String[] parts = fieldPath == null ? new String[0] : fieldPath.split("\\.");
        if (parts.length == 0 || parts.length > MAX_PATH_DEPTH
                || !Arrays.stream(parts).allMatch(part -> PATH_SEGMENT.matcher(part).matches())) {
            throw CommonErrors.invalid(String.valueOf(fieldPath), "Invalid field path: " + fieldPath);
        }
        Attribute attribute = objectMetadata.getAttributeByName(parts[0]);
        if (attribute != null) {
            if (parts.length == 1) {
                return new ResolvedField(attribute, attribute.getField(), attribute.isJsonField(), false);
            }
            if (!attribute.isJsonField()) {
                // As before this method: a nested path on a plain column filters on the column itself
                return new ResolvedField(attribute, attribute.getField(), false, false);
            }
            return new ResolvedField(attribute, buildJsonPath(attribute, parts), true, true);
        }
        Attribute customValues = objectMetadata.getAttributeByName(Constants.CUSTOM_VALUE_NAME);
        if (customValues != null && customValues.isJsonField() && isDeclaredReference(objectMetadata, parts[0])) {
            return new ResolvedField(customValues, buildJsonPath(customValues, Utils.ARRAYS.concat(
                    new String[]{Constants.CUSTOM_VALUE_NAME},
                    parts
            )), true, true);
        }
        throw CommonErrors.fieldNotFoundException(fieldPath, fieldPath + " not found");
    }

    /** True when the object declares a JSON reference with this name (the fieldName of an attributeRef). */
    private static boolean isDeclaredReference(ObjectMetadata objectMetadata, String referenceName) {
        return referenceName.endsWith(Constants.REFERENCE_SUFFIX) && objectMetadata.getAttributeRefs().stream()
                .anyMatch(attribute -> referenceName.equals(attribute.getAttributeRef().getFieldName()));
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
        return buildJsonPath(attribute, parts).convertFrom(JavaTypeMappingFactory.DYNAMIC_JSON_CONVERTER::from);
    }

    /**
     * The JSON path as text, with no converter: a filter binds its values as they are. The converted field of
     * {@link #buildJsonField} only reads (select, sort): binding a value to it fails ("Conversion function not
     * implemented").
     */
    private static Field<String> buildJsonPath(Attribute attribute, String[] parts) {
        // parts[0] = name of column JSON
        // parts[1..n] = path of JSON
        Field<?> result = attribute.getField();

        for (int i = 1; i < parts.length; i++) {
            boolean isLast = (i == parts.length - 1);
            if (isLast) {
                return DSL.field(
                        "({0} ->> {1})",
                        String.class,
                        result,
                        DSL.inline(parts[i])
                );
            }
            result = DSL.field(
                    "({0} -> {1})",
                    Object.class,
                    result,
                    DSL.inline(parts[i])
            );
        }
        return DSL.field("{0}", String.class, result);
    }

    public static List<String> pathAsArray(String path) {
        return Arrays.stream(path.split("\\.")).collect(Collectors.toList());
    }

    public static String pathAsFieldName(String path) {
        return path.split("\\.")[0];
    }

}

