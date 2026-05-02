package com.msm.core.hook.anontation;

import com.msm.core.commons.Condition;
import com.msm.core.commons.Constants;
import com.msm.core.hook.AlwaysTrueCondition;

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
