package com.msm.core.hook.context;

import com.msm.core.commons.Utils;

public class ActionHookContextFactory {

    public static <T> HookContext<T> from(ActionRequest<T> req) {
        HookContext<T> ctx = new HookContext<>();
        ctx.setObjectName(req.getObjectName());
        ctx.setAction(req.getAction());
        ctx.setPayload(req.getPayload());
        ctx.setObjectId(req.getObjectId());
        ctx.setAdditionalData(req.getAdditionalParameter());

        //Copy context data
        if(Utils.CL.isNotEmpty(req.getContextKey())) {
            ctx.getContextKey().putAll(req.getContextKey());
        }

        return ctx;
    }
}