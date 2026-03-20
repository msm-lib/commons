package com.msm.core.validate.attr;

import com.msm.core.commons.Utils;
import com.msm.core.validate.AttributeTypeHandler;
import com.msm.core.validate.domain.AttributeType;

import java.util.Set;

public class NumberTypeHandler implements AttributeTypeHandler {

    private final String dataType;
    static {
        AttributeTypeHandlerFactory.register(new NumberTypeHandler("Long"));
        AttributeTypeHandlerFactory.register(new NumberTypeHandler("Integer"));
        AttributeTypeHandlerFactory.register(new NumberTypeHandler("Double"));
    }

    private NumberTypeHandler(String dataType){
        this.dataType = dataType;
    }

    @Override
    public boolean supports(Class<?> t) {
        return Number.class.isAssignableFrom(t) || t.isPrimitive();
    }

    @Override
    public String dataType() {
        return dataType;
    }

    @Override
    public boolean isValid(Object value) {
        try {
            Double.parseDouble(value.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Object normalizeValue(Object value) {
        return null;
    }
}