package com.msm.core.hook.common;

import com.msm.core.hook.context.HookContext;

import java.util.List;

public interface AsyncExecutor {
    void executeAsync(List<HookHandler> handlers, HookContext ctx);
}
