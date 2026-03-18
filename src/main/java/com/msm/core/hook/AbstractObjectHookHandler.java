package com.msm.core.hook;

import com.msm.core.commons.Utils;

public abstract class AbstractObjectHookHandler implements HookHandler {
    public String type() {
        return handlerName();
    }

    @Override
    public String integrationType() {
        return Utils.STR.uncapitalize(getClass().getSimpleName());
    }

    public abstract String handlerName();
}

