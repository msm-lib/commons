package com.msm.core.hook.common;

public interface Condition {
    boolean matches(ExecutionContext ctx);
}
