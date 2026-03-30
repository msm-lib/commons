package com.msm.core.hook.common;

import com.msm.core.hook.HookDefinition;
import com.msm.core.hook.HookPhase;

public interface ObjectHookMetadata extends ExecutionMetadata {
    HookPhase getHookPhase();
    HookDefinition getDefinition();
    default HookCondition getCondition() {
        return ctx -> true;
    }
}
