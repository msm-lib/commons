package com.msm.core.metadata.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AttributeDefinitionRef {
    String fieldName() default "";
    String objectRef() default "";
    String usageType() default "Reference";
    RefData refData() default @RefData();
}
