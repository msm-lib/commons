package com.msm.core.validate.attr;

import com.msm.core.validate.AttributeTypeHandler;
import com.msm.core.validate.domain.AttributeType;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class AttributeTypeHandlerFactory {
    private static final Map<String, AttributeTypeHandler> HANDLERS = new ConcurrentHashMap<>() ;

    public static AttributeTypeHandler getHandler(String type) {

        AttributeTypeHandler attributeTypeHandler =  HANDLERS.get(type);
        if (attributeTypeHandler == null) {
            throw new IllegalArgumentException("No handler for type: " + type);
        }

        return attributeTypeHandler;
    }

    public static void register(AttributeTypeHandler attributeTypeHandler) {
        HANDLERS.putIfAbsent(attributeTypeHandler.dataType(), attributeTypeHandler);
    }



}
