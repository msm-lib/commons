package com.msm.core.hook;

import com.msm.core.hook.common.TransactionHook;
import lombok.Setter;

public class TransactionUtils {
    @Setter
    private static TransactionHook hook = new NoopTransactionHook();

    public static void runAfterCommit(Runnable task) {
        hook.runAfterCommit(task);
    }
}
