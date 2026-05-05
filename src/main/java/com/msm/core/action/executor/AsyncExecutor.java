package com.msm.core.action.executor;

import com.msm.core.action.hook.HookDefinitionExecutor;
import com.msm.core.action.context.ActionContext;

import java.util.List;

public interface AsyncExecutor {
    <X> void executeAsync(List<HookDefinitionExecutor> handlers, ActionContext<X> ctx);
}
