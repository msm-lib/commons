package com.msm.core.hook;

import com.msm.core.commons.Condition;
import com.msm.core.commons.Utils;
import com.msm.core.commons.ValueConvertFactory;
import com.msm.core.exceptions.Errors;
import com.msm.core.hook.common.ActionExecutor;
import com.msm.core.hook.common.HookEngine;
import com.msm.core.hook.context.ActionContext;
import com.msm.core.hook.context.KeyDimensionResolver;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class DefaultActionExecutor implements ActionExecutor {
    private final HookEngine hookEngine;

    @Override
    public <T, X> T execute(ActionContext<X> request) {
        Objects.requireNonNull(request.getAction(), "The action cannot be null");
        ActionDefinitionExecutor handler = getActionHandler(request);
        if (Objects.isNull(handler)) {
            throw Errors.unsupported("Unsupported action: " + request.getAction());
        }
        if(request.isDisableHookEvent()) {
            return handler.execute(request);
        }

        hookEngine.execute(request, HookPhase.BEFORE_EVENT);
        T returnObject = handler.execute(request);
        request.setResult(returnObject);
        hookEngine.execute(request, HookPhase.AFTER_EVENT);
        hookEngine.execute(request,  HookPhase.AFTER_COMMIT_EVENT);
        return returnObject;
    }

    public <X> ActionDefinitionExecutor getActionHandler(ActionContext<X> request) {

        String key = KeyDimensionResolver.resolveHandlerKey(request);
        List<ActionDefinitionExecutor> executors = ActionHandlerFactory.getHandler(key);

        if (Utils.CL.isEmpty(executors)) {
            executors = ActionHandlerFactory.getHandler(KeyDimensionResolver.resolveHandlerDefaultKey(request));
        }

        if (Utils.CL.isEmpty(executors)) {
            throw Errors.unsupported("No handler found for: " + key);
        }

        List<ActionDefinitionExecutor> defaultMatched = executors.stream()
                .filter(ex -> isMatchDefaultCondition(ex.getCondition()))
                .toList();
        List<ActionDefinitionExecutor> extendMatched = executors.stream()
                .filter(ex -> isMatchExtendCondition(ex.getCondition(), request))
                .toList();

        if (extendMatched.isEmpty()) {
            if (defaultMatched.isEmpty()) {
                throw Errors.unsupported("No handler matched condition for: " + key);
            }
            return defaultMatched.getFirst();
        }

        if (extendMatched.size() > 1) {
            throw new IllegalStateException("Multiple handlers matched for: " + key);
        }

        return extendMatched.getFirst();
    }

    private boolean isMatchDefaultCondition(Condition<?> condition) {
        return (condition instanceof AlwaysTrueCondition);
    }
    private boolean isMatchExtendCondition(Condition<ActionContext<?>> condition, ActionContext<?> request) {
        return !isMatchDefaultCondition(condition) && Objects.nonNull(condition) && condition.matches(request);
    }

    public <O> O execute(ActionContext<?> request, Class<O> outputType) {

        Objects.requireNonNull(request.getAction(), "The action cannot be null");
        ActionDefinitionExecutor handler = getActionHandler(request);

        if (handler == null) {
            throw Errors.unsupported("No handler found");
        }
        O returnObject = handler.execute(request);
        //Runtime type-safe check
        if (!ValueConvertFactory.normalizeDataType(outputType).isInstance(returnObject)) {
            throw Errors.invalid("Invalid return type. Expected " + outputType.getSimpleName());
        }

        return outputType.cast(returnObject);
    }
}
