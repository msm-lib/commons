package com.msm.core.hook;

public interface HookHandler extends ObjectTypeMetadata {
    void execute(HookContext ctx) throws Exception;
}