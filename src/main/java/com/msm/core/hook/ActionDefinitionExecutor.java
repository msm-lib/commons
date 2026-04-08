package com.msm.core.hook;

import com.msm.core.hook.anontation.Handler;
import com.msm.core.hook.common.Condition;
import com.msm.core.hook.context.ActionRequest;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.function.Function;

@Slf4j
@Data
@Builder
@RequiredArgsConstructor
public class ActionDefinitionExecutor {
    private final Condition condition;
    private final Function<ActionRequest<?>, ?> invoker;
    public <X, T> X execute(ActionRequest<T> actionRequest) {
        return (X) invoker.apply(actionRequest);
    }

    public static ActionDefinitionExecutor create(Object bean, Method method, Handler handler, Condition condition) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle handle = lookup.unreflect(method).bindTo(bean);

            Function<ActionRequest<?>, ?> invoker = request -> {
                try {
                    return handle.invoke(request);
                } catch (Throwable e) {
                    throw Lombok.sneakyThrow(e);
                }
            };

            return new ActionDefinitionExecutor(condition, invoker);
        } catch (IllegalAccessException e) {
            throw Lombok.sneakyThrow(e);
        }
    }
}
