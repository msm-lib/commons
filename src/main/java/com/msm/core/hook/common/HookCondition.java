package com.msm.core.hook.common;

import com.msm.core.hook.context.HookContext;

public interface HookCondition {
    boolean test(HookContext ctx);
}
