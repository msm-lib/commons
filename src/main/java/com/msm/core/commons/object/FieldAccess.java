package com.msm.core.commons.object;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

public class FieldAccess {
    private static final ConcurrentHashMap<String, MethodHandle> GETTERS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, MethodHandle> SETTERS = new ConcurrentHashMap<>();

    public static Object get(Object obj, String fieldName) {
        try {
            String key = obj.getClass().getName() + "." + fieldName;
            MethodHandle getter = GETTERS.computeIfAbsent(key, k -> {
                try {
                    Field f = obj.getClass().getDeclaredField(fieldName);
                    f.setAccessible(true);
                    return MethodHandles.lookup().unreflectGetter(f);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            return getter.invoke(obj);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void set(Object obj, String fieldName, Object value) {

        try {

            String key = obj.getClass().getName() + "." + fieldName;
            MethodHandle setter = SETTERS.computeIfAbsent(key, k -> {
                try {
                    Field f = obj.getClass().getDeclaredField(fieldName);
                    f.setAccessible(true);
                    return MethodHandles.lookup().unreflectSetter(f);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            setter.invoke(obj, value);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
