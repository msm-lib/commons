package com.msm.core.dynamicquery.mapping;

import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Field;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DynamicQueryFieldValueMapper {
    public static Map<Field<?>, Object> toInsertMap(ObjectMetadata meta, Map<String,Object> values) {
        List<Attribute> attrs = meta.getAttributes();
        Map<Field<?>, Object> fieldValues = new LinkedHashMap<>();
        for (Attribute attr : attrs) {
            if (!values.containsKey(attr.getFieldName())) continue;
            Object rawValue = values.get(attr.getFieldName());
            Object casted = attr.cast(rawValue);
            if (casted != null) {
                Field<?> field = attr.getField();
                fieldValues.put(field, casted);
            }
        }

        return fieldValues;
    }

    public static Map<Field<?>, Object> toUpdateMap(
            ObjectMetadata meta,
            Map<String,Object> values) {

        Map<Field<?>, Object> map = new LinkedHashMap<>();
        values.forEach((k, v) -> {
            Attribute attribute = meta.getAttributeByName(k);
            if (attribute != null) {
                Field<?> field = attribute.getField();
                map.put(field, attribute.cast(v));
            }
        });

        return map;
    }
}
