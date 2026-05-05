package com.msm.core.action.executor;

import com.msm.core.action.context.ActionContext;

public interface ActionExecutor {
    <T, X> T execute(ActionContext<X> request);
}
