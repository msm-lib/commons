package com.msm.core.hook.common;

import com.msm.core.hook.context.ActionRequest;

public interface ActionExecutor {
    <T, X> T execute(ActionRequest<X> request);
}
