package com.msm.core.hook;

import java.util.List;

public interface AsyncExecutor {
    void executeAsync(List<HookHandler> handlers, HookContext ctx);
}
