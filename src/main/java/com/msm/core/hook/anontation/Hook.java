package com.msm.core.hook.anontation;

import com.msm.core.hook.AlwaysTrueCondition;
import com.msm.core.hook.HookPhase;
import com.msm.core.commons.Condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Hook {
    String object();
    String action();
    HookPhase phase();
    int order() default 0;
    ExtendContextKey[] keyContexts() default {};
    Class<? extends Condition> condition() default AlwaysTrueCondition.class;
    boolean stopOnError() default true;
}