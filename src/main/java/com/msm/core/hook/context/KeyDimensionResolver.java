package com.msm.core.hook.context;

import com.msm.core.commons.Constants;
import com.msm.core.hook.common.ObjectHookMetadata;

import java.util.*;
import java.util.stream.Collectors;

public final class KeyDimensionResolver {

    public static String resolve(ObjectHookMetadata ctx) {
        Map<ContextKey<?>, String> dims = new LinkedHashMap<>();
        dims.put(ContextKeys.OBJECT_NAME, ctx.getObjectName());
        dims.put(ContextKeys.ACTION, ctx.getAction());
        dims.put(ContextKeys.PHASE, ctx.getHookPhase().name());

        ctx.getContextData().forEach((k, v) -> dims.put(k, String.valueOf(v)));

        return build(dims.values());
    }

    public static String resolve(HookContext ctx) {
        Map<ContextKey<?>, String> dims = new LinkedHashMap<>();
        dims.put(ContextKeys.OBJECT_NAME, ctx.getObjectName());
        dims.put(ContextKeys.ACTION, ctx.getAction());
        dims.put(ContextKeys.PHASE, ctx.getPhase().name());

        ctx.getContextData().forEach((k, v) -> dims.put(k, String.valueOf(v)));
        return build(dims.values());
    }

    public static String getDefaultKey(HookContext ctx) {
        return build(List.of(
                Constants.GENERIC_OBJECT_HOOK_NAME,
                ctx.getAction(),
                ctx.getPhase().name()
        ));
    }

    public static String getDefaultKey(ObjectHookMetadata ctx) {
        return build(List.of(
                Constants.GENERIC_OBJECT_HOOK_NAME,
                ctx.getAction(),
                ctx.getHookPhase().name()
        ));
    }

    private static String build(Collection<String> keys) {
        return keys.stream()
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .collect(Collectors.joining(":"));
    }

}
