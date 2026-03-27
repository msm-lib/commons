package com.msm.core.hook.context;

import com.msm.core.commons.Utils;

public class ActionHookContextFactory {

    public static HookContext from(ActionRequest req) {
        HookContext ctx = new HookContext();
        ctx.setObjectName(req.getObjectName());
        ctx.setAction(req.getAction());
        ctx.setPayload(req.getPayload());

        //Copy context data
        if(Utils.CL.isNotEmpty(req.getContextData())) {
            ctx.getContextData().putAll(req.getContextData());
        }

        return ctx;
    }
}