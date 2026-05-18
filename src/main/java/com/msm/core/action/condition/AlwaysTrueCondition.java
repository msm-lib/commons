package com.msm.core.action.condition;

import com.msm.core.action.context.ExecutionContext;
import com.msm.core.commons.Condition;

public class AlwaysTrueCondition implements Condition<ExecutionContext<?>> {
    @Override
    public boolean matches(ExecutionContext<?> ctx) {
        return true;
    }
}
