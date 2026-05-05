package com.msm.core.action.transaction;

public interface TransactionHook {

    void runAfterCommit(Runnable task);

}