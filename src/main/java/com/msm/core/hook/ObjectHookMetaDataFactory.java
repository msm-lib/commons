package com.msm.core.hook;

import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@SuppressWarnings("unchecked")
public final class ObjectHookMetaDataFactory {

    private static final Map<String, Object> INSTANCES = new ConcurrentHashMap<>();

    private ObjectHookMetaDataFactory() {}

    public static <T> void register(String compositeKey, T instance) {
        ((List<T>) INSTANCES.computeIfAbsent(compositeKey, key -> new ArrayList<>())).add(instance);
    }

    public static <T> void register(String name, String action, String phase, T instance) {
        ((List<T>) INSTANCES.computeIfAbsent(Utils.STR.format(Constants.OBJECT_HOOK_KEY, name, Utils.STR.lowCase(action), Utils.STR.lowCase(phase)), key -> new ArrayList<>())).add(instance);
    }

    public static <T> List<T> get(String name, String action, String phase) {
        Object service = INSTANCES.get(Utils.STR.format(Constants.OBJECT_HOOK_KEY, name, Utils.STR.lowCase(action), phase));
        service = Objects.isNull(service) ? INSTANCES.get(Utils.STR.format(Constants.OBJECT_HOOK_KEY, Constants.GENERIC_OBJECT_HOOK_NAME, Utils.STR.lowCase(action), Utils.STR.lowCase(phase))) : service;
        if (Objects.isNull(service)) {
            log.warn("Service type mismatch for groupName: {}, action: {}, phase: {} ", name, action, phase);
            return null;
        }

        return (List<T>) service;
    }

    public static boolean contains(String compositeKey) {
        return INSTANCES.containsKey(compositeKey);
    }
    public static boolean contains(String name, String action, String phase) {
        return INSTANCES.containsKey(Utils.STR.format(Constants.OBJECT_HOOK_KEY, name, Utils.STR.lowCase(action), Utils.STR.lowCase(phase)));
    }

}