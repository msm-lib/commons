package com.msm.core.hook;

import com.msm.core.commons.Utils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ActionHandlerFactory {
    /**
     * Allow List because some case we can create same handler key but other {@link com.msm.core.commons.Condition}
     */
    private final static Map<String, List<ActionDefinitionExecutor>> INSTANCES = new ConcurrentHashMap<>();

    public static void register(String compositeKey, ActionDefinitionExecutor instance) {
        INSTANCES.computeIfAbsent(compositeKey, key -> new ArrayList<>()).add(instance);
    }

    public static List<ActionDefinitionExecutor> getHandler(String key) {
        List<ActionDefinitionExecutor> service = INSTANCES.get(key);
        if (Objects.isNull(service)) {
            log.warn("No handler found for key: {}", key);
            return Utils.CL.newArrayList();
        }

        return service;
    }

    public static boolean contains(String compositeKey) {
        return INSTANCES.containsKey(compositeKey);
    }
}
