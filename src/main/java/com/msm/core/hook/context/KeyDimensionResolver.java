package com.msm.core.hook.context;

import com.msm.core.commons.Constants;
import com.msm.core.hook.HookPhase;
import com.msm.core.hook.anontation.*;

import java.util.*;
import java.util.stream.Collectors;

public final class KeyDimensionResolver {

    public static String resolve(AnnotationConfig annotationConfig) {
        Map<ContextKey<?>, String> dims = new LinkedHashMap<>();
        dims.put(ContextKeys.HOOK_PREFIX, Constants.HOOK_PREFIX);
        dims.put(ContextKeys.OBJECT_NAME, annotationConfig.object());
        dims.put(ContextKeys.ACTION, annotationConfig.action());
        dims.put(ContextKeys.PHASE, annotationConfig.phase().name());

        for (ExtendContextKey kv : annotationConfig.extendContextKey()) {
            dims.put(ContextKey.of(kv.key()), kv.value());
        }

        return build(dims.values());
    }

    public static String resolveHandler(AnnotationConfig annotationConfig) {
        Map<ContextKey<?>, String> dims = new LinkedHashMap<>();
        dims.put(ContextKeys.ACTION_PREFIX, Constants.ACTION_PREFIX);
        dims.put(ContextKeys.OBJECT_NAME, annotationConfig.object());
        dims.put(ContextKeys.ACTION, annotationConfig.action());

        for (ExtendContextKey kv : annotationConfig.extendContextKey()) {
            dims.put(ContextKey.of(kv.key()), kv.value());
        }

        return build(dims.values());
    }

    public static <T> String resolve(ActionRequest<T> ctx, HookPhase phase) {
        Map<ContextKey<?>, String> dims = new LinkedHashMap<>();
        dims.put(ContextKeys.HOOK_PREFIX, Constants.HOOK_PREFIX);
        dims.put(ContextKeys.OBJECT_NAME, ctx.getObjectName());
        dims.put(ContextKeys.ACTION, ctx.getAction());
        dims.put(ContextKeys.PHASE, phase.name());

        ctx.getContextKey().forEach((k, v) -> dims.put(k, String.valueOf(v)));
        return build(dims.values());
    }

    public static <T> String resolveDefaultKey(ActionRequest<T> ctx, HookPhase phase) {
        return build(List.of(
                Constants.HOOK_PREFIX,
                Constants.GENERIC_OBJECT_NAME,
                ctx.getAction(),
                phase.name()
        ));
    }


    public static String resolve(Handler handler) {
        Map<ContextKey<?>, String> dims = new LinkedHashMap<>();
        dims.put(ContextKeys.ACTION_PREFIX, Constants.ACTION_PREFIX);
        dims.put(ContextKeys.OBJECT_NAME, handler.object());
        dims.put(ContextKeys.ACTION, handler.action());

        for (ExtendContextKey kv : handler.keyContexts()) {
            dims.put(ContextKey.of(kv.key()), kv.value());
        }

        return build(dims.values());
    }

    public static <T> String resolve(ActionRequest<T> actionRequest) {
        Map<ContextKey<?>, String> dims = new LinkedHashMap<>();
        dims.put(ContextKeys.ACTION_PREFIX, Constants.ACTION_PREFIX);
        dims.put(ContextKeys.OBJECT_NAME, actionRequest.getObjectName());
        dims.put(ContextKeys.ACTION, actionRequest.getAction());
        actionRequest.getContextKey().forEach((k, v) -> {
            dims.put(k, (String) v);
        });

        return build(dims.values());
    }

    public static <T> String resolveDefaultKey(ActionRequest<T> actionRequest) {
        Map<ContextKey<?>, String> dims = new LinkedHashMap<>();
        dims.put(ContextKeys.ACTION_PREFIX, Constants.ACTION_PREFIX);
        dims.put(ContextKeys.OBJECT_NAME, Constants.GENERIC_OBJECT_NAME);
        dims.put(ContextKeys.ACTION, actionRequest.getAction());
        actionRequest.getContextKey().forEach((k, v) -> {
            dims.put(k, (String) v);
        });

        return build(dims.values());
    }

    private static String build(Collection<String> keys) {
        return keys.stream()
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .collect(Collectors.joining(":"));
    }

}
