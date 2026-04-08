package com.msm.core.hook;

import com.msm.core.commons.Utils;
import com.msm.core.exceptions.Errors;
import com.msm.core.exceptions.UnsupportedException;
import com.msm.core.hook.common.Condition;
import com.msm.core.hook.common.HookEngine;
import com.msm.core.hook.common.ActionExecutor;
import com.msm.core.hook.context.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class DefaultActionExecutor implements ActionExecutor {
    private final HookEngine hookEngine;

    @SneakyThrows
    @Override
    public <T> T execute(ActionRequest request) {
        Objects.requireNonNull(request.getAction(), "The action cannot be null");
        ActionDefinitionExecutor handler = getActionHandler(request);
        if (Objects.isNull(handler)) {
            throw Errors.unsupported("Unsupported action: " + request.getAction());
        }
        HookContext hookContext = request.getHookContextConvertStrategy().convert(request);
        hookContext.setPhase(HookPhase.BEFORE_EVENT);
        hookEngine.execute(hookContext);
        T returnObject = handler.execute(request);
        hookContext.nextPhase(HookPhase.AFTER_EVENT);
        hookContext.setCurrentRecord(returnObject);
        hookEngine.execute(hookContext);
        hookContext.nextPhase(HookPhase.AFTER_COMMIT_EVENT);
        hookEngine.execute(hookContext);
        return returnObject;
    }

    public ActionDefinitionExecutor getActionHandler(ActionRequest request) {

        String key = KeyDimensionResolver.resolve(request);
        List<ActionDefinitionExecutor> executors = ActionHandlerFactory.getHandler(key);

        if (Utils.CL.isEmpty(executors)) {
            executors = ActionHandlerFactory.getHandler(KeyDimensionResolver.resolveDefaultKey(request));
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
//            throw new IllegalStateException("Multiple handlers matched for: " + key + " → " +
//                    matched.stream()
//                            .map(ex -> ex.getMethod().getName())
//                            .toList()
//            );
            throw new IllegalStateException("Multiple handlers matched for: " + key);
        }

        return extendMatched.getFirst();
    }

    private boolean isMatchDefaultCondition(Condition condition) {
        return (condition instanceof AlwaysTrueCondition);
    }
    private boolean isMatchExtendCondition(Condition condition, ActionRequest request) {
        return !isMatchDefaultCondition(condition) && Objects.nonNull(condition) && condition.matches(request);
    }
}
