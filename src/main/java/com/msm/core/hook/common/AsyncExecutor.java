package com.msm.core.hook.common;

import com.msm.core.hook.HookDefinitionExecutor;
import com.msm.core.hook.context.HookContext;

import java.util.List;

public interface AsyncExecutor {
    <X> void executeAsync(List<HookDefinitionExecutor> handlers, HookContext<X> ctx);
}
