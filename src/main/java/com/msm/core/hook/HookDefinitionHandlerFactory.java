package com.msm.core.hook;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class HookDefinitionHandlerFactory {
    private final static Map<String, List<HookDefinitionHandler>> INSTANCES = new ConcurrentHashMap<>();

    public static void register(String compositeKey, HookDefinitionHandler instance) {
        INSTANCES.computeIfAbsent(compositeKey, key -> new ArrayList<>()).add(instance);
    }

    public static void register(String compositeKey, List<HookDefinitionHandler> instances) {
        INSTANCES.put(compositeKey, instances);
    }

    public static List<HookDefinitionHandler> get(String key) {
        List<HookDefinitionHandler> service = INSTANCES.get(key);
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