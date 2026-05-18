package com.msm.core.action.hook;

import com.msm.core.action.context.ActionContext;
import com.msm.core.action.context.KeyDimensionResolver;
import com.msm.core.action.executor.AsyncExecutor;
import com.msm.core.action.transaction.TransactionUtils;
import com.msm.core.commons.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Default implementation of {@link HookEngine} responsible for resolving
 * and executing hooks for a given object and execution phase.
 *
 * <p>This engine retrieves hook metadata from {@code ObjectServiceFactory},
 * sorts the hooks based on their defined order, and resolves corresponding
 * {@code HookHandler} instances for execution.</p>
 *
 * <p>Execution behavior depends on the {@link HookPhase}:</p>
 * <ul>
 *   <li>For phases other than {@code AFTER_COMMIT_EVENT}, handlers are executed sequentially
 *       in ascending order.</li>
 *   <li>For {@code AFTER_COMMIT_EVENT}, handlers are executed asynchronously using
 *       the provided {@code AsyncExecutor}.</li>
 * </ul>
 *
 * <p>This design allows flexible extension of business logic by plugging in
 * multiple handlers and controlling their execution order and timing.</p>
 *
 * <p><b>Threading behavior:</b></p>
 * <ul>
 *   <li>Synchronous execution is performed in the caller thread.</li>
 *   <li>Asynchronous execution delegates processing to {@code AsyncExecutor}.</li>
 * </ul>
 *
 * <p><b>Dependencies:</b></p>
 * <ul>
 *   <li>{@code AsyncExecutor} - used for executing hooks asynchronously</li>
 *   <li>{@code ObjectServiceFactory} - used to resolve hook metadata and handlers</li>
 * </ul>
 *
 * @see HookEngine
 * @see HookPhase
 */
@Slf4j
@RequiredArgsConstructor
public final class DefaultHookEngine implements HookEngine {

    private final AsyncExecutor asyncExecutor;

    @Override
    public <X> void execute(ActionContext<X> ctx, HookPhase phase) {
        String key = KeyDimensionResolver.resolveHookKey(ctx, phase);
        List<HookDefinitionExecutor> hooks = HookDefinitionHandlerFactory.get(key);
        if(Utils.CL.isEmpty(hooks)) {
            key = KeyDimensionResolver.resolveHookDefaultKey(ctx, phase);
            hooks = HookDefinitionHandlerFactory.get(key);
        }

        //Force load hook system run for any object
        //No override by any resource
        String systemKey = KeyDimensionResolver.resolveSystemKey(ctx, phase);
        List<HookDefinitionExecutor> systemHooks = HookDefinitionHandlerFactory.get(systemKey);
        if(Utils.CL.isNotEmpty(systemHooks)) {
            hooks.addAll(systemHooks);
        }

        if (HookPhase.AFTER_COMMIT_EVENT.equals(phase)) {
            List<HookDefinitionExecutor> finalHooks = hooks;
            TransactionUtils.runAfterCommit(() -> asyncExecutor.executeAsync(finalHooks, ctx));
        } else {
            for (HookDefinitionExecutor h : hooks) {
                h.execute(ctx);
            }
        }
    }
}