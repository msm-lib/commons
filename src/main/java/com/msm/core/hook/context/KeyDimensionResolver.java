package com.msm.core.hook.context;

import com.msm.core.commons.Constants;
import com.msm.core.hook.anontation.Handler;
import com.msm.core.hook.anontation.Hook;
import com.msm.core.hook.anontation.ContextKey;

import java.util.*;
import java.util.stream.Collectors;

public final class KeyDimensionResolver {

    public static String resolve(Hook hook) {
        Map<com.msm.core.hook.context.ContextKey<?>, String> dims = new LinkedHashMap<>();
        dims.put(ContextKeys.HOOK_PREFIX, Constants.HOOK_PREFIX);
        dims.put(ContextKeys.OBJECT_NAME, hook.object());
        dims.put(ContextKeys.ACTION, hook.action());
        dims.put(ContextKeys.PHASE, hook.phase().name());

        for (ContextKey kv : hook.keyContexts()) {
            dims.put(com.msm.core.hook.context.ContextKey.of(kv.key()), kv.value());
        }

        return build(dims.values());
    }

    public static <T> String resolve(HookContext<T> ctx) {
        Map<com.msm.core.hook.context.ContextKey<?>, String> dims = new LinkedHashMap<>();
        dims.put(ContextKeys.HOOK_PREFIX, Constants.HOOK_PREFIX);
        dims.put(ContextKeys.OBJECT_NAME, ctx.getObjectName());
        dims.put(ContextKeys.ACTION, ctx.getAction());
        dims.put(ContextKeys.PHASE, ctx.getPhase().name());

        ctx.getContextKey().forEach((k, v) -> dims.put(k, String.valueOf(v)));
        return build(dims.values());
    }

    public static <T> String resolveDefaultKey(HookContext<T> ctx) {
        return build(List.of(
                Constants.HOOK_PREFIX,
                Constants.GENERIC_OBJECT_NAME,
                ctx.getAction(),
                ctx.getPhase().name()
        ));
    }


    public static String resolve(Handler handler) {
        Map<com.msm.core.hook.context.ContextKey<?>, String> dims = new LinkedHashMap<>();
        dims.put(ContextKeys.ACTION_PREFIX, Constants.ACTION_PREFIX);
        dims.put(ContextKeys.OBJECT_NAME, handler.object());
        dims.put(ContextKeys.ACTION, handler.action());

        for (ContextKey kv : handler.keyContexts()) {
            dims.put(com.msm.core.hook.context.ContextKey.of(kv.key()), kv.value());
        }

        return build(dims.values());
    }

    public static <T> String resolve(ActionRequest<T> actionRequest) {
        Map<com.msm.core.hook.context.ContextKey<?>, String> dims = new LinkedHashMap<>();
        dims.put(ContextKeys.ACTION_PREFIX, Constants.ACTION_PREFIX);
        dims.put(ContextKeys.OBJECT_NAME, actionRequest.getObjectName());
        dims.put(ContextKeys.ACTION, actionRequest.getAction());
        actionRequest.getContextKey().forEach((k, v) -> {
            dims.put(k, (String) v);
        });

        return build(dims.values());
    }

    public static <T> String resolveDefaultKey(ActionRequest<T> actionRequest) {
        Map<com.msm.core.hook.context.ContextKey<?>, String> dims = new LinkedHashMap<>();
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
