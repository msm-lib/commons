package com.msm.core.hook;

import com.msm.core.commons.Utils;
import com.msm.core.hook.common.AsyncExecutor;
import com.msm.core.hook.common.HookEngine;
import com.msm.core.hook.common.HookHandler;
import com.msm.core.hook.common.ObjectHookMetadata;
import com.msm.core.hook.context.*;
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
     * @param ctx the {@code HookContext} containing execution state and shared data
     */
//    public void execute(HookContext ctx) {
//        List<String> keys = hookKeyStrategy.build(ctx);
//
//        List<ObjectHookMetadata> hooks = keys
//                .stream()
//                .filter(ObjectHookMetaDataFactory::contains)
//                .flatMap(key -> ObjectHookMetaDataFactory.get(key).stream())
//                .distinct()
//                .toList();
//
////        List<ObjectHookMetadata> hooks = ObjectHookMetaDataFactory.getOrDefault(ctx.hookMetaDataKey(), ctx.defaultHookMetaDataKey());
//        if(Utils.CL.isEmpty(hooks)) {
//            log.warn("Event hook not found for object: {}", objectName);
//            return;
//        }
//        List<HookHandler> handlers = Utils.CL.emptyIfNull(hooks)
//                .stream()
//                .sorted(Comparator.comparingInt(o -> o.definition().getOrder()))
//                .map(hook -> hook.definition().getHookHandler())
//                .toList();
//
//        if (HookPhase.AFTER_COMMIT_EVENT.equals(ctx.getPhase())) {
//            asyncExecutor.executeAsync(handlers, ctx.forAfterCommit());
//        } else {
//            for (HookHandler h : handlers) {
//                h.handle(ctx, null);
//            }
//        }
//    }


    public void execute(HookContext ctx) {
        String key = KeyDimensionResolver.resolve(ctx);
        List<ObjectHookMetadata> hooks = ObjectHookMetaDataFactory.get(key);
        if(Utils.CL.isEmpty(hooks)) {
            log.warn("Event hook not found for object: {}", ctx.getObjectName());
            key = KeyDimensionResolver.getDefaultKey(ctx);
            hooks = ObjectHookMetaDataFactory.get(key);
        }

        List<HookHandler> hookHandlers = Utils.CL.emptyIfNull(hooks)
                .stream()
                .sorted(Comparator.comparingInt(o -> o.getDefinition().getOrder()))
                .map(hook -> hook.getDefinition().getHookHandler())
                .toList();

        if (HookPhase.AFTER_COMMIT_EVENT.equals(ctx.getPhase())) {
            asyncExecutor.executeAsync(hookHandlers, ctx);
        } else {
            for (HookHandler h : hookHandlers) {
                h.handle(ctx, null);
            }
        }
    }
}