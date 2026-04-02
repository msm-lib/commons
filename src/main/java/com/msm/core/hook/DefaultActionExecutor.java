package com.msm.core.hook;

import com.msm.core.exceptions.UnsupportedException;
import com.msm.core.hook.common.HookEngine;
import com.msm.core.hook.common.ActionExecutor;
import com.msm.core.hook.context.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

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
            throw new UnsupportedException("Unsupported action: " + request.getAction());
        }

        HookContext hookContext = request.getHookContextConvertStrategy().convert(request);
        hookContext.setPhase(HookPhase.BEFORE_EVENT);
        hookEngine.execute(hookContext);
        T returnObject = handler.execute(request);
//        T returnObject;
//        try {
//            returnObject = handler.execute(request);
//        } catch (Throwable ex) {
//            hookContext.setError(ex);
//            hookContext.setPhase(HookPhase.ERROR_EVENT);
//            hookEngine.execute(hookContext);
//            hookContext.setPhase(HookPhase.AFTER_EVENT);
//            hookEngine.execute(hookContext);
//
//            throw Lombok.sneakyThrow(ex);
//        }

        hookContext.nextPhase(HookPhase.AFTER_EVENT);
        hookContext.setCurrentRecord(returnObject);
        hookEngine.execute(hookContext);
        hookContext.nextPhase(HookPhase.AFTER_COMMIT_EVENT);
        hookEngine.execute(hookContext);
        return returnObject;
    }

    private ActionDefinitionExecutor getActionHandler(ActionRequest request) {
        String key = KeyDimensionResolver.resolve(request);
        if(!ActionHandlerFactory.contains(key)) {
            key = KeyDimensionResolver.resolveDefaultKey(request);
        }
        return ActionHandlerFactory.getHandler(key);
    }
}
