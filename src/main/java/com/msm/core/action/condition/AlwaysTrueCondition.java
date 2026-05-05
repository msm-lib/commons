package com.msm.core.action.condition;

import com.msm.core.commons.Condition;
import com.msm.core.action.context.ExecutionContext;

public class AlwaysTrueCondition implements Condition<ExecutionContext<?>> {
    @Override
    public boolean matches(ExecutionContext<?> ctx) {
        return true;
    }
}
