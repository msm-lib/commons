package com.msm.core.action.transaction;

public class NoopTransactionHook implements TransactionHook {

    @Override
    public void runAfterCommit(Runnable task) {
        task.run();
    }
}
