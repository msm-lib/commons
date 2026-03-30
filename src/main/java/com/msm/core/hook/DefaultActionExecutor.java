package com.msm.core.hook;

import com.msm.core.exceptions.UnsupportedActionException;
import com.msm.core.hook.common.HookEngine;
import com.msm.core.hook.common.ActionExecutor;
import com.msm.core.hook.common.ActionHandler;
import com.msm.core.hook.context.*;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public class DefaultActionExecutor implements ActionExecutor {
    private final HookEngine hookEngine;

    @Override
    public <T> T execute(ActionRequest request) {

        ActionHandler handler = ActionHandlerFactory.getHandler(request.getAction());
        if (Objects.isNull(handler)) {
            throw new UnsupportedActionException("Unsupported action: " + request.getAction());
        }

        HookContext hookContext = request.getHookContextConvertStrategy().convert(request);
        hookContext.setPhase(HookPhase.BEFORE_EVENT);
        hookEngine.execute(hookContext);
        T returnObject = handler.handle(request);
        hookContext.nextPhase(HookPhase.AFTER_EVENT);
        hookContext.setCurrentRecord(returnObject);
        hookEngine.execute(hookContext);
        hookContext.nextPhase(HookPhase.AFTER_COMMIT_EVENT);
        hookEngine.execute(hookContext);
        return returnObject;
    }
}
