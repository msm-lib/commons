package com.msm.core.action.executor;

import com.msm.core.action.context.ActionContext;
import com.msm.core.action.hook.HookDefinitionExecutor;

import java.util.List;

public interface AsyncExecutor {
    <X> void executeAsync(List<HookDefinitionExecutor> handlers, ActionContext<X> ctx);
}
