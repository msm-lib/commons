package com.msm.core.hook.anontation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ContextKey {
    String key();
    String value();
}
