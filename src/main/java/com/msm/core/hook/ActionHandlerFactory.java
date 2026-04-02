package com.msm.core.hook;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ActionHandlerFactory {
    private final static Map<String, ActionDefinitionExecutor> INSTANCES = new ConcurrentHashMap<>();

    public static void register(String compositeKey, ActionDefinitionExecutor instance) {
        INSTANCES.put(compositeKey, instance);
    }

    public static ActionDefinitionExecutor getHandler(String key) {
        ActionDefinitionExecutor service = INSTANCES.get(key);
        if (Objects.isNull(service)) {
            log.warn("No action found for key: {}", key);
        }
        return service;
    }

    public static boolean contains(String compositeKey) {
        return INSTANCES.containsKey(compositeKey);
    }
}
