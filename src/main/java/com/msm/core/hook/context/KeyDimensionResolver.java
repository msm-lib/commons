package com.msm.core.hook.context;

import com.msm.core.commons.Constants;
import com.msm.core.hook.anontation.Hook;
import com.msm.core.hook.anontation.ContextKey;

import java.util.*;
import java.util.stream.Collectors;

public final class KeyDimensionResolver {

    public static String resolve(Hook hook) {
        Map<com.msm.core.hook.context.ContextKey<?>, String> dims = new LinkedHashMap<>();
        dims.put(ContextKeys.OBJECT_NAME, hook.object());
        dims.put(ContextKeys.ACTION, hook.action());
        dims.put(ContextKeys.PHASE, hook.phase().name());

        for (ContextKey kv : hook.keyContexts()) {
            dims.put( com.msm.core.hook.context.ContextKey.of(kv.key()), kv.value());
        }

        return build(dims.values());
    }

    public static String resolve(HookContext ctx) {
        Map<com.msm.core.hook.context.ContextKey<?>, String> dims = new LinkedHashMap<>();
        dims.put(ContextKeys.OBJECT_NAME, ctx.getObjectName());
        dims.put(ContextKeys.ACTION, ctx.getAction());
        dims.put(ContextKeys.PHASE, ctx.getPhase().name());

        ctx.getContextKey().forEach((k, v) -> dims.put(k, String.valueOf(v)));
        return build(dims.values());
    }

    public static String getDefaultKey(HookContext ctx) {
        return build(List.of(
                Constants.GENERIC_OBJECT_HOOK_NAME,
                ctx.getAction(),
                ctx.getPhase().name()
        ));
    }

    private static String build(Collection<String> keys) {
        return keys.stream()
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .collect(Collectors.joining(":"));
    }

}
