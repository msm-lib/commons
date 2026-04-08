package com.msm.core.hook;

import com.msm.core.hook.anontation.Hook;
import com.msm.core.hook.common.Condition;
import com.msm.core.hook.context.HookContext;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.function.Consumer;

@Slf4j
@Data
@Builder
@RequiredArgsConstructor
public class HookDefinitionExecutor {
    private final Condition condition;
    private final Consumer<HookContext> invoker;
    private final int order;

    private final boolean stopOnError;

    public void execute(HookContext ctx) {
        if (!condition.matches(ctx)) {
            return;
        }
        invoker.accept(ctx);
    }

    public static HookDefinitionExecutor create(Object bean, Method method, Hook hook, Condition condition, boolean stopOnError) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle handle = lookup.unreflect(method).bindTo(bean);
            Consumer<HookContext> invoker = ctx -> {
                try {
                    handle.invoke(ctx);
                } catch (Throwable e) {
                    log.error("Exception while invoking {}", method, e);
                    if (stopOnError) {
                        throw Lombok.sneakyThrow(e);
                    }
                }
            };

            return new HookDefinitionExecutor(condition, invoker, hook.order(), stopOnError);
        } catch (IllegalAccessException e) {
            throw Lombok.sneakyThrow(e);
        }
    }
}
