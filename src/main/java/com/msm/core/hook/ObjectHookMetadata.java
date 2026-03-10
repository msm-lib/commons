package com.msm.core.hook;

public interface ObjectHookMetadata extends ObjectTypeMetadata {
    String objectName();
    HookPhase phase();
    HookDefinition definition();
}
