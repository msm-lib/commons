package com.msm.core.hook;

import com.msm.core.hook.common.TransactionHook;

public class NoopTransactionHook implements TransactionHook {

    @Override
    public void runAfterCommit(Runnable task) {
        task.run();
    }
}
