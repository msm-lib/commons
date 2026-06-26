package com.msm.core.action.context;

import com.msm.core.action.annotations.ExtendContextKey;
import com.msm.core.action.hook.HookPhase;
import com.msm.core.commons.Constants;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class KeyDimensionResolver {

    public static String resolveHandlerKey(AnnotationUtility annotationConfig) {
        Map<ContextKey<?>, String> dims = new LinkedHashMap<>();
        dims.put(ContextKeys.ACTION_PREFIX, annotationConfig.getType());
        dims.put(ContextKeys.RESOURCE_NAME, annotationConfig.getResource());
        dims.put(ContextKeys.ACTION, annotationConfig.getAction());

        for (ExtendContextKey kv : annotationConfig.getExtendContextKey()) {
            dims.put(ContextKey.of(kv.key()), kv.value());
        }

        return build(dims.values());
    }

    public static String resolveHookKey(AnnotationUtility annotationConfig) {
        Map<ContextKey<?>, String> dims = new LinkedHashMap<>();
        dims.put(ContextKeys.HOOK_PREFIX, annotationConfig.getType());
        dims.put(ContextKeys.RESOURCE_NAME, annotationConfig.getResource());
        dims.put(ContextKeys.ACTION, annotationConfig.getAction());
        dims.put(ContextKeys.PHASE, annotationConfig.getPhase());

        for (ExtendContextKey kv : annotationConfig.getExtendContextKey()) {
            dims.put(ContextKey.of(kv.key()), kv.value());
        }

        return build(dims.values());
    }

    public static <T> String resolveSystemKey(ActionContext<T> ctx, HookPhase phase) {
        return build(List.of(
                Constants.HOOK_PREFIX,
                Constants.GENERIC_SYSTEM_OBJECT,
                ctx.getAction(),
                phase.name()
        ));
    }

    private static String build(Collection<String> keys) {
        return keys.stream()
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .collect(Collectors.joining(":"));
    }

    public static <T> String resolveHandlerKey(ExecutionContext<T> actionRequest) {
        Map<ContextKey<?>, String> dims = new LinkedHashMap<>();
        dims.put(ContextKeys.ACTION_PREFIX, Constants.HANDLER_PREFIX);
        dims.put(ContextKeys.RESOURCE_NAME, actionRequest.getResource());
        dims.put(ContextKeys.ACTION, actionRequest.getAction());
        actionRequest.getContextKey().forEach((k, v) -> {
            dims.put(k, (String) v);
        });

        return build(dims.values());
    }

    public static <T> String resolveHandlerDefaultKey(ExecutionContext<T> actionContext) {
        Map<ContextKey<?>, String> dims = new LinkedHashMap<>();
        dims.put(ContextKeys.ACTION_PREFIX, Constants.HANDLER_PREFIX);
        dims.put(ContextKeys.RESOURCE_NAME, Constants.GENERIC_RESOURCE_NAME);
        dims.put(ContextKeys.ACTION, actionContext.getAction());
        actionContext.getContextKey().forEach((k, v) -> {
            dims.put(k, (String) v);
        });

        return build(dims.values());
    }

    public static <T> String resolveHookKey(ExecutionContext<T> ctx, HookPhase phase) {
        Map<ContextKey<?>, String> dims = new LinkedHashMap<>();
        dims.put(ContextKeys.HOOK_PREFIX, Constants.HOOK_PREFIX);
        dims.put(ContextKeys.RESOURCE_NAME, ctx.getResource());
        dims.put(ContextKeys.ACTION, ctx.getAction());
        dims.put(ContextKeys.PHASE, phase.name());

        ctx.getContextKey().forEach((k, v) -> dims.put(k, String.valueOf(v)));
        return build(dims.values());
    }

    public static <T> String resolveHookDefaultKey(ActionContext<T> ctx, HookPhase phase) {
        return build(List.of(
                Constants.HOOK_PREFIX,
                Constants.GENERIC_RESOURCE_NAME,
                ctx.getAction(),
                phase.name()
        ));
    }
}
