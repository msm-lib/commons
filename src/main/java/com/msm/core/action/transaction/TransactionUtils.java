package com.msm.core.action.transaction;

import lombok.Setter;

public class TransactionUtils {
    @Setter
    private static TransactionHook hook = new NoopTransactionHook();

    public static void runAfterCommit(Runnable task) {
        hook.runAfterCommit(task);
    }
}
