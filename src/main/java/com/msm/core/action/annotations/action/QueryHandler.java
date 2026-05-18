package com.msm.core.action.annotations.action;

import com.msm.core.action.annotations.ExtendContextKey;
import com.msm.core.action.condition.AlwaysTrueCondition;
import com.msm.core.commons.Condition;
import com.msm.core.commons.Constants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Handler(action = "", resource = Constants.RESOURCE_QUERY_PREFIX)
public @interface QueryHandler {
    String action();
    ExtendContextKey[] keyContexts() default {};
    Class<? extends Condition> condition() default AlwaysTrueCondition.class;
}
