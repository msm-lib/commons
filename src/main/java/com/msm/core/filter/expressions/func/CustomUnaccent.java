package com.msm.core.filter.expressions.func;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.type.StandardBasicTypes;

public class CustomUnaccent implements FunctionContributor {
    @Override
    public void contributeFunctions(FunctionContributions fc) {
        SqmFunctionRegistry registry = fc.getFunctionRegistry();

        registry.registerPattern(
                "unaccent",
                "unaccent(?1)",
                fc.getTypeConfiguration()
                        .getBasicTypeRegistry()
                        .resolve(StandardBasicTypes.STRING)
        );
    }
}