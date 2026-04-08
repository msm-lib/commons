package com.msm.core.hook.context;

import com.msm.core.commons.Converter;

public class DefaultHookContextConvertStrategy<T> implements Converter<ActionRequest<T>, HookContext<T>> {

    @Override
    public HookContext<T> convert(ActionRequest<T> request) {
        return ActionHookContextFactory.from(request);
    }
}
