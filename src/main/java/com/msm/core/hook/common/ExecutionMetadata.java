package com.msm.core.hook.common;

import com.msm.core.hook.context.ContextKey;

import java.util.Map;

public interface ExecutionMetadata {
    String getObjectName();
    String getAction();
    Map<ContextKey<?>, Object> getContextKey();
}
