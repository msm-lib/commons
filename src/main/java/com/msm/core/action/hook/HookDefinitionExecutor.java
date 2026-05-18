package com.msm.core.action.hook;

import com.msm.core.action.annotations.hook.Hook;
import com.msm.core.action.context.ActionContext;
import com.msm.core.commons.Condition;
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
    private final Condition<ActionContext<?>> condition;
    private final Consumer<ActionContext<?>> invoker;
    private final int order;

    private final boolean stopOnError;

    public <X> void execute(ActionContext<X> ctx) {
        if (!condition.matches(ctx)) {
            return;
        }
        invoker.accept(ctx);
    }

    public static HookDefinitionExecutor create(Object bean, Method method, Hook hook, Condition<ActionContext<?>> condition, boolean stopOnError) {
        return create(bean, method, hook.order(), condition, stopOnError);
    }

    public static HookDefinitionExecutor create(Object bean, Method method, int order, Condition<ActionContext<?>> condition, boolean stopOnError) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle handle = lookup.unreflect(method).bindTo(bean);
            Consumer<ActionContext<?>> invoker = ctx -> {
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
