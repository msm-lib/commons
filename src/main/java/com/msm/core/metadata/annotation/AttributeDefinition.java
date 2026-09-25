package com.msm.core.metadata.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.FIELD})
public @interface AttributeDefinition {
    enum NoEnum {}
    String fieldType() default "";
    String fieldName() default "";
    String columnName() default "";
    String regex() default "";
    boolean required() default false;
    boolean freeText() default false;
    String defaultValue() default "";
    long maxLength() default -1;
    long maxValue() default -1;
    long minValue() default -1;
    long maxSize() default -1;
    Class<? extends Enum<?>> enumType() default NoEnum.class;
    AttributeDefinitionRef attributeRef() default @AttributeDefinitionRef();
}