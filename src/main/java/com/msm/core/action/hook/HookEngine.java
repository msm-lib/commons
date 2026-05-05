package com.msm.core.action.hook;

import com.msm.core.action.context.ActionContext;

/**
 * Defines the contract for a hook execution engine.
 *
 * <p>The {@code HookEngine} is responsible for executing a set of hooks
 * associated with a given object or context. Hooks typically represent
 * custom logic that can be plugged into specific points of a workflow,
 * allowing extensibility and modular behavior.</p>
 *
 * <p>Implementations of this interface may resolve and execute multiple
 * {@code HookDefinition}s based on the provided {@code objectName}, and
 * apply them in a defined order using the given {@code HookContext}.</p>
 *
 * <p>This interface is commonly used in systems that support dynamic
 * extensions, event-driven processing, or lifecycle interceptors.</p>
 */
public interface HookEngine {
    /**
     * Executes all hooks associated with the specified object name.
     * The implementation is expected to:
     * <ul>
     *   <li>Execute them in order</li>
     *   <li>Use {@code ctx} to share data</li>
     * </ul>
     */
    <X> void execute(ActionContext<X> ctx, HookPhase phase);
}
