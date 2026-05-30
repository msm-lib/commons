package com.msm.core.action.annotations.hook.crud.bulk;

import com.msm.core.action.annotations.ExtendContextKey;
import com.msm.core.action.annotations.hook.Hook;
import com.msm.core.action.condition.AlwaysTrueCondition;
import com.msm.core.action.hook.HookPhase;
import com.msm.core.commons.Condition;
import com.msm.core.commons.Constants;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Hook(action = Constants.Action.BULK_DELETE, phase = HookPhase.AFTER_EVENT)
public @interface HookAfterBulkDelete {
    String resource() default Constants.GENERIC_RESOURCE_NAME;
    int order() default 0;
    ExtendContextKey[] keyContexts() default {};
    Class<? extends Condition> condition() default AlwaysTrueCondition.class;
    boolean stopOnError() default true;
}
