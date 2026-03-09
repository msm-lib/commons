package com.msm.core.hook;

public interface HookEngine {
    void execute(String objectName, HookContext ctx) throws Exception;
}
