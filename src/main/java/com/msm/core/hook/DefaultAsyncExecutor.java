package com.msm.core.hook;

import com.msm.core.hook.common.AsyncExecutor;
import com.msm.core.hook.context.HookContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executor;

@RequiredArgsConstructor
@Slf4j
public class DefaultAsyncExecutor implements AsyncExecutor {
    private final Executor hookTaskExecutor;

    @Override
    public void executeAsync(List<HookDefinitionHandler> handlers, HookContext ctx) {
        hookTaskExecutor.execute(() -> {
            for (HookDefinitionHandler h : handlers) {
                try {
                    h.execute(ctx);
                } catch (Exception e) {
                    log.error("Error executing hook task", e);
                }
            }
        });
    }
}
