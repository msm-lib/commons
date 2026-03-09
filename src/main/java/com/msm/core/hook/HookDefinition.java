package com.msm.core.hook;

public record HookDefinition(
        String handlerName,
        int order
) {}