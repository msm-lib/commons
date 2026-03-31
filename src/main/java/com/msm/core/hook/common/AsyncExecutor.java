package com.msm.core.hook.common;

import com.msm.core.hook.HookDefinitionHandler;
import com.msm.core.hook.context.HookContext;

import java.util.List;

public interface AsyncExecutor {
    void executeAsync(List<HookDefinitionHandler> handlers, HookContext ctx);
}
