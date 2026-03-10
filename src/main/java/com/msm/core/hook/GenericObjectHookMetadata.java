package com.msm.core.hook;

import com.msm.core.commons.Utils;

public abstract class GenericObjectHookMetadata implements ObjectHookMetadata {

    @Override
    public String type() {
        return Utils.STR.format(Constant.OBJECT_HOOK_KEY, objectName(), phase());
    }

    @Override
    public String objectName() {
        return Constant.GENERIC_OBJECT_HOOK_NAME;
    }

    @Override
    public String integrationType() {
        return Utils.STR.uncapitalize(getClass().getSimpleName());
    }
}
