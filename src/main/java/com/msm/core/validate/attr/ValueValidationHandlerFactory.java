package com.msm.core.validate.attr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class ValueValidationHandlerFactory {
    private static final Map<String, ValueValidationHandler> HANDLERS = new ConcurrentHashMap<>() ;

    static {
        register(new DefaultNoHandler());
        register(new BooleanValueHandler());
        register(new DateTimeValueHandler());
        register(new DateValueHandler());
        register(new StringValueHandler());
        register(new UuidTypeHandler());
        register(new InstantValueHandler());
        register(new NumberValueHandler("BigDecimal"));
        register(new NumberValueHandler("Long"));
        register(new NumberValueHandler("Integer"));
        register(new NumberValueHandler("Double"));
    }

    public static ValueValidationHandler getHandler(String type) {
        ValueValidationHandler valueValidationHandler =  HANDLERS.get(type);
        if (valueValidationHandler == null) {
//            throw new IllegalArgumentException("No handler for type: " + type);
            return HANDLERS.get("default");
        }

        return valueValidationHandler;
    }

    public static void register(ValueValidationHandler valueValidationHandler) {
        Object existing = HANDLERS.putIfAbsent(valueValidationHandler.supportType(), valueValidationHandler);
        if (Objects.isNull(existing)) {
            log.warn("Key {} already has a registered instance. Skipping overwrite.", valueValidationHandler.supportType());
        }
    }
}
