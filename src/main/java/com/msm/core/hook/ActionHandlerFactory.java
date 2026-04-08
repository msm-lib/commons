package com.msm.core.hook;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ActionHandlerFactory {
    private final static Map<String, List<ActionDefinitionExecutor>> INSTANCES = new ConcurrentHashMap<>();

    public static void register(String compositeKey, ActionDefinitionExecutor instance) {
        INSTANCES.computeIfAbsent(compositeKey, key -> new ArrayList<>()).add(instance);
    }

    public static List<ActionDefinitionExecutor> getHandler(String key) {
        List<ActionDefinitionExecutor> service = INSTANCES.get(key);
        if (Objects.isNull(service)) {
            log.warn("No hooks found for key: {}", key);
            return Collections.emptyList();
        }

        return service;
    }

    public static boolean contains(String compositeKey) {
        return INSTANCES.containsKey(compositeKey);
    }
}
