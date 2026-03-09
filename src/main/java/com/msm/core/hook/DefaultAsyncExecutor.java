package com.msm.core.hook;

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
                    h.execute(ctx);
                } catch (Exception e) {
                    log.error("Error executing hook task", e);
                }
            }
        });
    }
}
