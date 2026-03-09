package com.msm.core.hook;

public interface HookObjectMetadata extends ObjectTypeMetadata {
    String objectName();
    HookPhase phase();
    HookDefinition definition();
}
