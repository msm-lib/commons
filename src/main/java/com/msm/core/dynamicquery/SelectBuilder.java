package com.msm.core.dynamicquery;

import com.msm.core.filter.domain.ObjectFilterRequest;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Field;
import org.jooq.SelectField;

import java.util.List;
import java.util.stream.Collectors;

public final class SelectBuilder {
    private SelectBuilder() {}

    public static List<SelectField<?>> buildFields(ObjectFilterRequest request, ObjectMetadata objectMetadata) {
        if (request.getReturnFields() == null || request.getReturnFields().isEmpty()) {
            return objectMetadata.getAttributes().stream().map(attribute -> getSelectField(attribute.getFieldName(), objectMetadata)).collect(Collectors.toList());
        }

        return request.getReturnFields()
                .stream()
                .map(f -> getSelectField(f, objectMetadata))
                .collect(Collectors.toList());
    }

    static SelectField<?> getSelectField(String fieldPath, ObjectMetadata objectMetadata) {
        String[] parts = fieldPath.split("\\.");
        String alias = parts[0];
        if (parts.length > 1) {
            alias = String.join(".", parts);
        }

        Field<?> field = FieldResolver.resolve(fieldPath, objectMetadata);
        return field.as(alias);
    }
}
