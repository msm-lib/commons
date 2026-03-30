package com.msm.core.hook.context;

import com.msm.core.commons.Converter;

public class DefaultHookContextConvertStrategy implements Converter<ActionRequest, HookContext> {

    @Override
    public HookContext convert(ActionRequest request) {
        return ActionHookContextFactory.from(request);
    }
}
