package com.msm.core.hook.anontation;

import com.msm.core.commons.Constants;
import com.msm.core.hook.AlwaysTrueCondition;
import com.msm.core.hook.common.Condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Handler {
    String object() default "";
    String action();
    ContextKey[] keyContexts() default {};
    Class<? extends Condition> condition() default AlwaysTrueCondition.class;
    int order() default 0;
}