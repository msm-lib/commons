package com.msm.core.hook;

public interface ObjectHookMetadata extends ObjectTypeMetadata {
    String objectName();
    String action();
    HookPhase phase();
    HookDefinition definition();
}
