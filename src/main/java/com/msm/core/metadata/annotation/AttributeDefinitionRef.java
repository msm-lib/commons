package com.msm.core.metadata.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to define a reference or mapping relationship between the current attribute
 * and an external object or reference data source.
 * <p>
 * This annotation is designed to be embedded within other annotations (such as {@code AttributeDefinition})
 * to provide relational metadata for data linking, lookup, or validation.
 * </p>
 *
 * @author danhnh
 * @since 1.0
 */
@Target({}) // Intended for use solely as a member type within other annotations
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AttributeDefinitionRef {

    /**
     * The name of the target field being referenced in the source or target object.
     * Defaults to an empty string.
     *
     * @return the target field name
     */
    String fieldName() default "";

    /**
     * The identifier, class name, or reference path of the external object being linked.
     * Defaults to an empty string.
     *
     * @return the external object reference identifier
     */
    String objectRef() default "";

    /**
     * Specifies the type or strategy of the reference usage (e.g., "Reference", "Lookup", "Cascade").
     * Defaults to {@code "Reference"}.
     *
     * @return the reference usage type string
     */
    String usageType() default "Reference";

    /**
     * The detailed configuration or metadata for reference data fetching/validation.
     * Defaults to an empty {@link RefData} instance.
     *
     * @return the associated reference data configuration
     */
    RefData refData() default @RefData();

    /**
     * The profile or context name applied to this reference (e.g., scoping rules, visibility profiles).
     * Defaults to an empty string.
     *
     * @return the reference profile name
     */
    String refProfile() default "";
}