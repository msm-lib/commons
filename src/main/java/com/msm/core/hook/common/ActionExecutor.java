package com.msm.core.hook.common;

import com.msm.core.hook.context.ActionContext;

public interface ActionExecutor {
    <T, X> T execute(ActionContext<X> request);
}
