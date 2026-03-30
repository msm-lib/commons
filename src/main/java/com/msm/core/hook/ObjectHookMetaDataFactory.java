package com.msm.core.hook;

import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;
import com.msm.core.hook.common.ObjectHookMetadata;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public final class ObjectHookMetaDataFactory {
    private static final Map<String, List<ObjectHookMetadata>> INSTANCES = new ConcurrentHashMap<>();

    private ObjectHookMetaDataFactory() {}

    public static void register(String compositeKey, ObjectHookMetadata instance) {
        INSTANCES.computeIfAbsent(compositeKey, key -> new ArrayList<>()).add(instance);
    }

    public static void register(String name, String action, String phase, ObjectHookMetadata instance) {
        INSTANCES.computeIfAbsent(buildKey(name, action, phase), key -> new ArrayList<>()).add(instance);
    }

    public static  List<ObjectHookMetadata> get(String name, String action, String phase) {
        String key = buildKey(name, action, phase);
        List<ObjectHookMetadata> service = INSTANCES.get(key);
        service = Objects.isNull(service) ? INSTANCES.get(buildKey(Constants.GENERIC_OBJECT_HOOK_NAME, action, phase)) : service;
        if (Objects.isNull(service)) {
            log.warn("No hooks found for name: {}, action: {}, phase: {}", name, action, phase);
            return Collections.emptyList();
        }

        return service;
    }

    public static List<ObjectHookMetadata> get(String key) {
        List<ObjectHookMetadata> service = INSTANCES.get(key);
        if (Objects.isNull(service)) {
            log.warn("No hooks found for key: {}", key);
            return Collections.emptyList();
        }

        return service;
    }

    public static List<ObjectHookMetadata> getOrDefault(String key, String defaultHookKey) {
        List<ObjectHookMetadata> service = INSTANCES.get(key);
        service = Objects.isNull(service) ? INSTANCES.get(defaultHookKey) : service;
        if (Objects.isNull(service)) {
            log.warn("No hooks found for key: {}, or default key: {}", key, defaultHookKey);
            return Collections.emptyList();
        }

        return service;
    }

    public static boolean contains(String compositeKey) {
        return INSTANCES.containsKey(compositeKey);
    }
    public static boolean contains(String name, String action, String phase) {
        return INSTANCES.containsKey(buildKey(name, action, phase));
    }

    public static String buildKey(String... names) {
        return Arrays.stream(names).map(Utils.STR::lowCase).collect(Collectors.joining(":"));
    }
}