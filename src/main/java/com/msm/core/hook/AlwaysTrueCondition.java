package com.msm.core.hook;

import com.msm.core.commons.Condition;
import com.msm.core.hook.common.ExecutionContext;

public class AlwaysTrueCondition implements Condition<ExecutionContext> {
    @Override
    public boolean matches(ExecutionContext ctx) {
        return true;
    }
}
