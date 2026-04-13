package com.msm.core.dynamicquery.operator;

import com.msm.core.dynamicquery.operator.impl.*;
import com.msm.core.exceptions.Errors;
import com.msm.core.filter.domain.FilterOperator;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class OperatorFactory {
    private static final Map<FilterOperator, OperatorHandler> HANDLERS = new EnumMap<>(FilterOperator.class);

    static {
        register(FilterOperator.EQUALS, new EqOperatorHandler());
        register(FilterOperator.NOT_EQUALS, new NeOperatorHandler());
        register(FilterOperator.LIKE, new LikeOperatorHandler());
        register(FilterOperator.GREATER_THAN, new GtOperatorHandler());
        register(FilterOperator.GREATER_THAN_OR_EQUAL, new GteOperatorHandler());
        register(FilterOperator.LESS_THAN, new LtOperatorHandler());
        register(FilterOperator.LESS_THAN_OR_EQUAL, new LteOperatorHandler());
        register(FilterOperator.IN, new InOperatorHandler());
        register(FilterOperator.NOT_IN, new NotInOperatorHandler());
        register(FilterOperator.BETWEEN, new BetweenOperatorHandler());
    }

    public static void register(FilterOperator operator, OperatorHandler handler) {
        HANDLERS.put(operator, handler);
    }

    public static OperatorHandler get(FilterOperator operator) {
        OperatorHandler handler = HANDLERS.get(operator);
        if(Objects.nonNull(handler)){
            return handler;
        }

        throw Errors.unsupported("Unsupported operator: " + operator);
    }

}