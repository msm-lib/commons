package com.msm.core.hook.generic;

import com.msm.core.hook.common.ObjectHookMetadata;

public abstract class GenericCreateActionHook implements ObjectHookMetadata {
    @Override
    public String getAction() {
        return "create";
    }
}
