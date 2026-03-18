package com.msm.core.hook.generic;

import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;
import com.msm.core.hook.HookPhase;
import com.msm.core.hook.ObjectHookMetadata;

public abstract class GenericHookBeforeEvent implements ObjectHookMetadata {

    @Override
    public HookPhase phase() {
        return HookPhase.BEFORE_EVENT;
    }

    @Override
    public String type() {
        return Utils.STR.format(Constants.OBJECT_HOOK_KEY, objectName(), phase());
    }

    @Override
    public String objectName() {
        return Constants.GENERIC_OBJECT_HOOK_NAME;
    }

    @Override
    public String integrationType() {
        return Utils.STR.uncapitalize(getClass().getSimpleName());
    }
}
