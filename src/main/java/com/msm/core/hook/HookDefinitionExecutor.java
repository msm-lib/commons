package com.msm.core.hook;

import com.msm.core.commons.Condition;
import com.msm.core.hook.anontation.Hook;
import com.msm.core.hook.context.ActionRequest;
import lombok.Builder;
import lombok.Data;
import lombok.Lombok;
import lombok.RequiredArgsConstructor;
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
    private final Condition<ActionRequest<?>> condition;
    private final Consumer<ActionRequest<?>> invoker;
    private final int order;

    private final boolean stopOnError;

    public <X> void execute(ActionRequest<X> ctx) {
        if (!condition.matches(ctx)) {
            return;
        }
        invoker.accept(ctx);
    }

    public static HookDefinitionExecutor create(Object bean, Method method, Hook hook, Condition<ActionRequest<?>> condition, boolean stopOnError) {
        return create(bean, method, hook.order(), condition, stopOnError);
    }

    public static HookDefinitionExecutor create(Object bean, Method method, int order, Condition<ActionRequest<?>> condition, boolean stopOnError) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle handle = lookup.unreflect(method).bindTo(bean);
            Consumer<ActionRequest<?>> invoker = ctx -> {
                try {
                    handle.invoke(ctx);
                } catch (Throwable e) {
                    log.error("Exception while invoking {}", method);
                    if (stopOnError) {
                        throw Lombok.sneakyThrow(e);
                    }
                }
            };

            return new HookDefinitionExecutor(condition, invoker, order, stopOnError);
        } catch (IllegalAccessException e) {
            throw Lombok.sneakyThrow(e);
        }
    }
}
