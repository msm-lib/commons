package com.msm.core.hook;

import com.msm.core.hook.common.AsyncExecutor;
import com.msm.core.hook.context.ActionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executor;

@RequiredArgsConstructor
@Slf4j
public class DefaultAsyncExecutor implements AsyncExecutor {
    private final Executor hookTaskExecutor;

    @Override
    public <X> void executeAsync(List<HookDefinitionExecutor> handlers, ActionContext<X> ctx) {
        hookTaskExecutor.execute(() -> {
            for (HookDefinitionExecutor h : handlers) {
                try {
                    h.execute(ctx);
                } catch (Exception e) {
                    log.error("Error executing hook task", e);
                }
            }
        });
    }
}
