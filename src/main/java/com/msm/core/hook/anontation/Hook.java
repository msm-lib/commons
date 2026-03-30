package com.msm.core.hook.anontation;

import com.msm.core.hook.HookPhase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Hook {
    HookPhase phase();
    String object();
    String action();
    int order() default 0;
}