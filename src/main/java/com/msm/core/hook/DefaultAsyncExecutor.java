package com.msm.core.hook;

import com.msm.core.hook.common.AsyncExecutor;
import com.msm.core.hook.common.HookHandler;
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
    public void executeAsync(List<HookHandler> handlers, HookContext ctx) {
        hookTaskExecutor.execute(() -> {
            for (HookHandler h : handlers) {
                try {
                    h.handle(ctx, null);
                } catch (Exception e) {
                    log.error("Error executing hook task", e);
                }
            }
        });
    }
}
