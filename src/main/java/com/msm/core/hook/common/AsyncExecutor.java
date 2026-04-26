package com.msm.core.hook.common;

import com.msm.core.hook.HookDefinitionExecutor;
import com.msm.core.hook.context.ActionRequest;

import java.util.List;

public interface AsyncExecutor {
    <X> void executeAsync(List<HookDefinitionExecutor> handlers, ActionRequest<X> ctx);
}
