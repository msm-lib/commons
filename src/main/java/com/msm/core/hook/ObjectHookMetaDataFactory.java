package com.msm.core.hook;

import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings("unchecked")
public final class ObjectHookMetaDataFactory {
    private static final Map<String, List<Object>> INSTANCES = new ConcurrentHashMap<>();

    private ObjectHookMetaDataFactory() {}

    public static <T> void register(String compositeKey, T instance) {
        INSTANCES.computeIfAbsent(compositeKey, key -> new ArrayList<>()).add(instance);
    }

    public static <T> void register(String name, String action, String phase, T instance) {
        INSTANCES.computeIfAbsent(buildKey(name, action, phase), key -> new ArrayList<>()).add(instance);
    }

    public static <T> List<T> get(String name, String action, String phase) {
        String key = buildKey(name, action, phase);
        List<Object> service = INSTANCES.get(key);
        service = Objects.isNull(service) ? INSTANCES.get(buildKey(Constants.GENERIC_OBJECT_HOOK_NAME, action, phase)) : service;
        if (Objects.isNull(service)) {
            log.warn("No hooks found for name: {}, action: {}, phase: {}", name, action, phase);
            return Collections.emptyList();
        }

        return (List<T>) service;
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