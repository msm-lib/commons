package com.msm.core.hook;

import com.msm.core.commons.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public final class ObjectServiceFactory {

    private static final Map<String, Object> INSTANCES = new ConcurrentHashMap<>();

    private ObjectServiceFactory() {}

    public static <T> void register(String name, T instance) {
        INSTANCES.put(name, instance);
    }

    public static <T> void registerGroup(String groupType, T instance) {
        ((List<T>) INSTANCES.computeIfAbsent(groupType, key -> new ArrayList<>())).add(instance);
    }

    public static <T> T get(String name) {
        Object service = INSTANCES.get(name);
        if (Objects.isNull(service)) {
            throw new IllegalStateException("Service type mismatch for: " + name);
        }

        return (T) service;
    }

    public static <T> T get(String type, Class<T> clazz) {
        Object service = INSTANCES.get(type);

        if (Objects.isNull(service) || !clazz.isInstance(service)) {
            throw new IllegalStateException("Service type mismatch for: " + type);
        }

        return clazz.cast(service);
    }

    public static <T> List<T> getGroup(String groupName, String phase) {
        Object service = INSTANCES.get(Utils.STR.format(Constant.OBJECT_HOOK_KEY, groupName, phase));
        service = Objects.isNull(service) ? INSTANCES.get(Utils.STR.format(Constant.OBJECT_HOOK_KEY, Constant.GENERIC_OBJECT_HOOK_NAME, phase)) : service;
        if (Objects.isNull(service)) {
            throw new IllegalStateException("Service type mismatch for: " + groupName);
        }

        return (List<T>) service;
    }

    public static boolean contains(String name) {
        return INSTANCES.containsKey(name);
    }

}