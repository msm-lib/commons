package com.msm.core.validate.attr;

import com.msm.core.commons.DataConvertFactory;
import com.msm.core.validate.AttributeTypeHandler;

public final class BooleanTypeHandler implements AttributeTypeHandler {

    static {
        AttributeTypeHandlerFactory.register(new BooleanTypeHandler());
    }

    private BooleanTypeHandler(){}

    @Override
    public boolean supports(Class<?> t) {
        return DataConvertFactory.normalizeDataType(t) == Boolean.class;
    }

    @Override
    public String dataType() {
        return Boolean.class.getName();
    }

    @Override
    public boolean isValid(Object value) {
        String val = value.toString().toLowerCase();
        return val.equals("true") || val.equals("false");
    }

    @Override
    public Object normalizeValue(Object value) {
        return Boolean.parseBoolean(value.toString());
    }

}