package com.msm.core.hook;

import com.msm.core.commons.Utils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class HookDefinitionHandlerFactory {
    private final static Map<String, List<HookDefinitionExecutor>> INSTANCES = new ConcurrentHashMap<>();

    public static void register(String compositeKey, HookDefinitionExecutor instance) {
        INSTANCES.computeIfAbsent(compositeKey, key -> new ArrayList<>()).add(instance);
    }

    public static void register(String compositeKey, List<HookDefinitionExecutor> instances) {
        INSTANCES.put(compositeKey, instances);
    }

    public static List<HookDefinitionExecutor> get(String key) {
        List<HookDefinitionExecutor> service = INSTANCES.get(key);
        if (Objects.isNull(service)) {
            return Utils.CL.newArrayList();
        }

        return service;
    }

    public static boolean contains(String compositeKey) {
        return INSTANCES.containsKey(compositeKey);
    }
}