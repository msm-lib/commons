package com.msm.core.action.annotations.hook;

import com.msm.core.action.annotations.ExtendContextKey;
import com.msm.core.commons.Condition;
import com.msm.core.commons.Constants;
import com.msm.core.action.condition.AlwaysTrueCondition;
import com.msm.core.action.hook.HookPhase;
import com.msm.core.action.annotations.action.ActionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@ActionType(type = Constants.HOOK_PREFIX)
public @interface Hook {
    String resource() default Constants.GENERIC_RESOURCE_NAME;
    String action() default "";
    HookPhase phase();
    int order() default 0;
    ExtendContextKey[] keyContexts() default {};
    Class<? extends Condition> condition() default AlwaysTrueCondition.class;
    boolean stopOnError() default true;
}