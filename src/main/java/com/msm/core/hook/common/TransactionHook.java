package com.msm.core.hook.common;

public interface TransactionHook {

    void runAfterCommit(Runnable task);

}