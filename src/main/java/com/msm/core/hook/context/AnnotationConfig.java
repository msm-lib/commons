package com.msm.core.hook.context;

import com.msm.core.commons.Condition;
import com.msm.core.hook.AlwaysTrueCondition;
import com.msm.core.hook.HookPhase;
import com.msm.core.hook.anontation.ExtendContextKey;
import com.msm.core.hook.anontation.Handler;
import com.msm.core.hook.anontation.Hook;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public record AnnotationConfig(
        String object,
        String action,
        HookPhase phase,
        int order,
        ExtendContextKey[] extendContextKey,
        Class<? extends Condition> condition,
        boolean stopOnError
) {

    public static AnnotationConfig fromHookMethod(Method method) {
        return new AnnotationConfig(
                getHookAttr(method, "object", ""),
                getHookAttr(method, "action", ""),
                getHookAttr(method, "phase", null),
                getHookAttr(method, "order", 0),
                getHookAttr(method, "keyContexts", new ExtendContextKey[] {}),
                getHookAttr(method, "condition", AlwaysTrueCondition.class),
                getHookAttr(method, "stopOnError", true)
        );
    }

    private static <T> T getHookAttr(Method method, String methodName, T defaultValue) {
        Annotation[] annotations = method.getDeclaredAnnotations();
        for(Annotation annotation : annotations) {
            if (isAnnotation(annotation.annotationType(), Hook.class)) {
                return getHookAttr(annotation, methodName,  defaultValue);
            }
        }
        return defaultValue;
    }

    private static <T> T getHookAttr(Object ann, String methodName, T defaultValue) {
        try {
            Method m = getMethod(ann, methodName);
            if(m == null) {
                if(!(ann instanceof Hook)) {
                    Hook hook = ((Annotation) ann).annotationType().getAnnotation(Hook.class);
                    m = getMethod(hook, methodName);
                    if(m == null) return defaultValue;
                    Object value = m.invoke(hook);
                    return (T) value;
                }
                return defaultValue;
            }

            Object value = m.invoke(ann);
            return (T) value;
        } catch (IllegalAccessException | InvocationTargetException e) {
            return defaultValue;
        }
    }


    public static AnnotationConfig fromHandlerMethod(Method method) {
        return new AnnotationConfig(
                getHandlerAttr(method, "object", ""),
                getHandlerAttr(method, "action", ""),
                null,
                0,
                getHandlerAttr(method, "keyContexts", new ExtendContextKey[] {}),
                getHandlerAttr(method, "condition", AlwaysTrueCondition.class),
                true
        );
    }

    private static <T> T getHandlerAttr(Method method, String methodName, T defaultValue) {
        Annotation[] annotations = method.getDeclaredAnnotations();
        for(Annotation annotation : annotations) {
            if (isAnnotation(annotation.annotationType(), Handler.class)) {
                return getHandlerAttr(annotation, methodName,  defaultValue);
            }
        }
        return defaultValue;
    }

    private static <T> T getHandlerAttr(Object ann, String methodName, T defaultValue) {
        try {
            Method m = getMethod(ann, methodName);
            if(m == null) {
                if(!(ann instanceof Handler)) {
                    //get parent
                    Handler handler = ((Annotation) ann).annotationType().getAnnotation(Handler.class);
                    m = getMethod(handler, methodName);
                    if(m == null) return defaultValue;
                    Object value = m.invoke(handler);
                    return (T) value;
                }

                return defaultValue;
            }

            Object value = m.invoke(ann);
            return (T) value;
        } catch (IllegalAccessException | InvocationTargetException e) {
            return defaultValue;
        }

    }

    private static boolean isAnnotation(Class<? extends Annotation> type, Class<?> clazz) {
        if (type == clazz) return true;
        for (Annotation meta : type.getAnnotations()) {
            Class<? extends Annotation> metaType = meta.annotationType();
            if (metaType.getName().startsWith("java.lang")) continue;
            if (isAnnotation(metaType, clazz)) return true;
        }
        return false;
    }

    private static Method getMethod(Object ann, String name) {
        try {
            return ann.getClass().getMethod(name);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}