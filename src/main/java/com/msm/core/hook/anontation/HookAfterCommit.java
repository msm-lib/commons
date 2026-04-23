package com.msm.core.hook.anontation;

import com.msm.core.commons.Condition;
import com.msm.core.commons.Constants;
import com.msm.core.hook.AlwaysTrueCondition;
import com.msm.core.hook.HookPhase;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Hook(phase = HookPhase.AFTER_COMMIT_EVENT)
public @interface HookAfterCommit {
    String object() default Constants.GENERIC_OBJECT_NAME;
    String action();
    int order() default 0;
    ExtendContextKey[] keyContexts() default {};
    Class<? extends Condition> condition() default AlwaysTrueCondition.class;
    boolean stopOnError() default true;
}
