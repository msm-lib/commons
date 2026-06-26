package com.msm.core.security.annotations;

import com.msm.core.security.enums.PermissionAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SecuredPermissionAction {
    PermissionAction[] anyOf() default {};
}
