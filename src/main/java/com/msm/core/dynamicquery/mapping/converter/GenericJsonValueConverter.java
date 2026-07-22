package com.msm.core.dynamicquery.mapping.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.msm.core.commons.Utils;
import org.jooq.JSONB;
import org.jooq.impl.AbstractConverter;

import static com.msm.core.commons.GenericTypeResolverFactory.resolve;

public class GenericJsonValueConverter extends AbstractConverter<Object, Object> {

    public GenericJsonValueConverter() {
        super(Object.class, Object.class);
    }

    @Override
    public Object from(Object databaseObject) {

        if (databaseObject == null) {
            return null;
        }

        try {
            String json = databaseObject.toString();
            JsonNode node = Utils.O.read(json, new TypeReference<>() {});
            if (node.isObject()) {
                return Utils.O.toObject(json, resolve("Map<String,Object>"));
            }

            if (node.isArray()) {
                return Utils.O.toObject(json, resolve("List<Object>"));
            }

            if (node.isTextual()) {
                return node.asText();
            }

            if (node.isBoolean()) {
                return node.asBoolean();
            }

            if (node.isNumber()) {
                return node.numberValue();
            }

            return json;

        } catch (Exception e) {
            return databaseObject.toString();
        }
    }

    @Override
    public Object to(Object userObject) {
        if (userObject == null) return null;
        try {
            return JSONB.valueOf(Utils.O.toJsonString(userObject));
        } catch (Exception e) {
            return null;
        }
    }
}