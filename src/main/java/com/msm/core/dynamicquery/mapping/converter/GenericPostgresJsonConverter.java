package com.msm.core.dynamicquery.mapping.converter;

import com.fasterxml.jackson.databind.JavaType;
import com.msm.core.commons.Utils;
import com.msm.core.exceptions.Errors;
import org.jooq.JSONB;
import org.jooq.impl.AbstractConverter;

public class GenericPostgresJsonConverter<U> extends AbstractConverter<Object, U> {

    private final JavaType targetType;

    public GenericPostgresJsonConverter(Class<U> userType, JavaType targetType) {
        super(Object.class, userType);
        this.targetType = targetType;
    }

    @Override
    public U from(Object databaseObject) {
        if (databaseObject == null || databaseObject.toString() == null) {
            return null;
        }
        try {
            return Utils.O.toObject(databaseObject.toString(), targetType);
        } catch (Exception e) {
            throw Errors.castException("Cannot convert JSONB to " + targetType.getTypeName(), e);
        }
    }

    @Override
    public Object to(U userObject) {
        if (userObject == null) return null;
        try {
            return JSONB.valueOf(Utils.O.toJsonString(userObject));
        } catch (Exception e) {
            return null;
        }
    }
}
