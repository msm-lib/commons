package com.msm.core.security.annotations;

import com.msm.core.security.enums.SecurityDataScopeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SecuredField {
    SecurityDataScopeType[] value() default {};
}