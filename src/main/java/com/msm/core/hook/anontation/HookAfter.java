package com.msm.core.hook.anontation;

import com.msm.core.commons.Condition;
import com.msm.core.commons.Constants;
import com.msm.core.hook.AlwaysTrueCondition;
import com.msm.core.hook.HookPhase;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Hook(phase = HookPhase.AFTER_EVENT)
public @interface HookAfter {
    String resource() default Constants.GENERIC_RESOURCE_NAME;
    String action();
    int order() default 0;
    ExtendContextKey[] keyContexts() default {};
    Class<? extends Condition> condition() default AlwaysTrueCondition.class;
    boolean stopOnError() default true;
}
