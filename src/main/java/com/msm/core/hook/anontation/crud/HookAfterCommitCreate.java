package com.msm.core.hook.anontation.crud;

import com.msm.core.commons.Condition;
import com.msm.core.commons.Constants;
import com.msm.core.hook.AlwaysTrueCondition;
import com.msm.core.hook.HookPhase;
import com.msm.core.hook.anontation.ExtendContextKey;
import com.msm.core.hook.anontation.Hook;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Hook(action = Constants.Action.CREATE, phase = HookPhase.AFTER_COMMIT_EVENT)
public @interface HookAfterCommitCreate {
    String resource() default Constants.GENERIC_RESOURCE_NAME;
    int order() default 0;
    ExtendContextKey[] keyContexts() default {};
    Class<? extends Condition> condition() default AlwaysTrueCondition.class;
    boolean stopOnError() default true;
}
