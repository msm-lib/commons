package com.msm.core.hook;

import com.msm.core.commons.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
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
 * @see HookHandler
 * @see HookPhase
 */
@Slf4j
@RequiredArgsConstructor
public final class DefaultHookEngine implements HookEngine {

    private final AsyncExecutor asyncExecutor;

    /**
     * Executes hooks associated with the specified object name and context.
     *
     * <p>The method performs the following steps:</p>
     * <ol>
     *   <li>Retrieve hook metadata based on {@code objectName} and current phase</li>
     *   <li>Sort hooks by their {@code order} in ascending order</li>
     *   <li>Resolve corresponding {@code HookHandler} instances</li>
     *   <li>Execute handlers either synchronously or asynchronously depending on phase</li>
     * </ol>
     *
     * @param objectName the identifier used to determine which hooks should be executed
     * @param ctx the {@code HookContext} containing execution state and shared data
     * @throws Exception if any handler execution fails or resolution errors occur
     */
    public void execute(String objectName, HookContext ctx) throws Exception {
        List<ObjectHookMetadata> hooks = ObjectServiceFactory.getGroup(objectName, ctx.getPhase().name());
        if(Utils.CL.isEmpty(hooks)) {
            log.warn("No hook found for object: {}", objectName);
            return;
        }
        List<HookHandler> handlers = hooks
                .stream()
                .sorted(Comparator.comparingInt(o -> o.definition().getOrder()))
                .map(hook -> ObjectServiceFactory.get(hook.definition().getHandlerName(), HookHandler.class))
                .toList();

        if (HookPhase.AFTER_COMMIT_EVENT.equals(ctx.getPhase())) {
            asyncExecutor.executeAsync(handlers, ctx.forAfterCommit());
        } else {
            for (HookHandler h : handlers) {
                h.execute(ctx);
            }
        }
    }
}