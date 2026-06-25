package com.msm.core.metadata.annotation;

import com.msm.core.metadata.RelationalTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface ObjectDefinitionRef {
    String targetObject();
    String foreignKeyAttribute() default "";
    String targetAttribute() default "id";
    RelationalTypeEnum relationType() default RelationalTypeEnum.REFERENCE;
}