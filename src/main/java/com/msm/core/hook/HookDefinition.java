package com.msm.core.hook;

import com.msm.core.hook.common.HookHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a hook definition used to describe a handler and its execution order.
 *
 * <p>A hook is typically a pluggable piece of logic that can be executed at a certain
 * point in a workflow or lifecycle. This class defines which handler should be invoked
 * and the order in which it should run relative to other hooks.</p>
 *
 * <p>The {@code order} field determines execution priority:
 * lower values are executed first, while higher values are executed later.</p>
 *
 * <p>This class is annotated with Lombok annotations:</p>
 * <ul>
 *   <li>{@code @Data} - Generates getters, setters, equals, hashCode, and toString methods</li>
 *   <li>{@code @Builder} - Enables the builder pattern for object creation</li>
 *   <li>{@code @NoArgsConstructor} - Generates a no-argument constructor</li>
 *   <li>{@code @AllArgsConstructor} - Generates an all-arguments constructor</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>
 * HookDefinition hook = HookDefinition.builder()
 *     .handlerName("validateInput")
 *     .order(1)
 *     .build();
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HookDefinition {
    /**
     * The type of the handler that will be executed.
     * This typically corresponds to a method name, bean name,
     * or identifier used to resolve the actual logic.
     */
    private HookHandler hookHandler;

    /**
     * The execution order of this hook.
     * Hooks with lower order values are executed before those with higher values.
     */
    private int order;
}