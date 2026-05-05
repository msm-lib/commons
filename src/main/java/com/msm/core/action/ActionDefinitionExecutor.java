package com.msm.core.action;

import com.msm.core.commons.Condition;
import com.msm.core.action.context.ExecutionContext;
import com.msm.core.action.context.ActionContext;
import lombok.Builder;
import lombok.Data;
import lombok.Lombok;
import lombok.RequiredArgsConstructor;
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
    private final Condition<ActionContext<?>> condition;
    private final Function<ExecutionContext<?>, ?> invoker;
    public <X, T> X execute(ExecutionContext<T> actionRequest) {
        return (X) invoker.apply(actionRequest);
    }

    public static ActionDefinitionExecutor create(Object bean, Method method, Condition condition) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle handle = lookup.unreflect(method).bindTo(bean);

            Function<ExecutionContext<?>, ?> invoker = request -> {
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
