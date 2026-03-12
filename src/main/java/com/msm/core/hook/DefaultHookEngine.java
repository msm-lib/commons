package com.msm.core.hook;

import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
public final class DefaultHookEngine implements HookEngine {

    private final AsyncExecutor asyncExecutor;

    public void execute(String objectName, HookContext ctx) throws Exception {
        List<ObjectHookMetadata> hooks = ObjectServiceFactory.getGroup(objectName);
        List<HookHandler> handlers = hooks.stream().map(hook -> ObjectServiceFactory.get(hook.definition().handlerName(), HookHandler.class)).toList();

        if (HookPhase.AFTER_COMMIT_EVENT.equals(ctx.getPhase())) {
            asyncExecutor.executeAsync(handlers, ctx.forAfterCommit());
        } else {
            for (HookHandler h : handlers) {
                h.execute(ctx);
            }
        }
    }
}