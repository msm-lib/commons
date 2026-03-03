package com.msm.core.filter.operator;

import com.msm.core.filter.domain.FilterOperator;
import com.msm.core.filter.operator.impl.*;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class OperatorFactory {
    private static final Map<FilterOperator, OperatorHandler> HANDLERS = new EnumMap<>(FilterOperator.class);

    static {
        register(FilterOperator.EQ, new EqOperatorHandler());
        register(FilterOperator.NE, new NeOperatorHandler());
        register(FilterOperator.LIKE, new LikeOperatorHandler());
        register(FilterOperator.GT, new GtOperatorHandler());
        register(FilterOperator.GTE, new GteOperatorHandler());
        register(FilterOperator.LT, new LtOperatorHandler());
        register(FilterOperator.LTE, new LteOperatorHandler());
        register(FilterOperator.IN, new InOperatorHandler());
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
        throw new IllegalArgumentException("Unsupported operator: " + operator);
    }

}