package com.msm.core.hook;

import com.msm.core.exceptions.UnsupportedException;
import com.msm.core.hook.anontation.Hook;
import com.msm.core.hook.context.HookContext;
import lombok.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.function.Consumer;

@Data
@Builder
@RequiredArgsConstructor
public class HookDefinitionHandler {
    private final Consumer<HookContext> invoker;
    private final int order;

    public void execute(HookContext ctx) {
        invoker.accept(ctx);
    }

    public static HookDefinitionHandler createExecutor(Object bean, Method method, Hook hook) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle handle = lookup.unreflect(method).bindTo(bean);
            Consumer<HookContext> invoker = ctx -> {
                try {
                    handle.invoke(ctx);
                } catch (Throwable e) {
                    throw new UnsupportedException("Exception while invoking " + method.getName(), e);
                }
            };

            return new HookDefinitionHandler(invoker, hook.order());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
