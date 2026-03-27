package com.msm.core.hook.generic.create;

import com.msm.core.commons.Constants;
import com.msm.core.hook.HookPhase;
import com.msm.core.hook.generic.GenericCreateActionHook;

public abstract class GenericActionHookAfterEvent extends GenericCreateActionHook {

    public HookPhase getHookPhase() {
        return HookPhase.AFTER_EVENT;
    }

    public String getObjectName() {
        return Constants.GENERIC_OBJECT_HOOK_NAME;
    }
}
