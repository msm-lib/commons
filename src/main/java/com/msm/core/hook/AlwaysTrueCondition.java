package com.msm.core.hook;

import com.msm.core.hook.common.Condition;
import com.msm.core.hook.common.ExecutionContext;

public class AlwaysTrueCondition implements Condition {
    @Override
    public boolean matches(ExecutionContext ctx) {
        return true;
    }
}
