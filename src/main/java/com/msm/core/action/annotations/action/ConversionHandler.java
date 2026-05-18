package com.msm.core.action.annotations.action;

import com.msm.core.action.annotations.ExtendContextKey;
import com.msm.core.action.condition.AlwaysTrueCondition;
import com.msm.core.commons.Condition;
import com.msm.core.commons.Constants;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Documented
@Handler(action = Constants.OBJECT_CONVERSION_ACTION)
//@ActionType(type = "Conversion")
public @interface ConversionHandler {
    String resource();
//    String action() default Constants.OBJECT_CONVERSION_ACTION;
    ExtendContextKey[] keyContexts() default {};
    Class<? extends Condition> condition() default AlwaysTrueCondition.class;
}
