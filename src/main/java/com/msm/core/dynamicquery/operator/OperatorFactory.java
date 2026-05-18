package com.msm.core.dynamicquery.operator;

import com.msm.core.dynamicquery.operator.impl.BetweenOperatorHandler;
import com.msm.core.dynamicquery.operator.impl.ContainsOneOfOperatorHandler;
import com.msm.core.dynamicquery.operator.impl.ContainsOperatorHandler;
import com.msm.core.dynamicquery.operator.impl.EqOperatorHandler;
import com.msm.core.dynamicquery.operator.impl.GtOperatorHandler;
import com.msm.core.dynamicquery.operator.impl.GteOperatorHandler;
import com.msm.core.dynamicquery.operator.impl.InOperatorHandler;
import com.msm.core.dynamicquery.operator.impl.LikeOperatorHandler;
import com.msm.core.dynamicquery.operator.impl.LtOperatorHandler;
import com.msm.core.dynamicquery.operator.impl.LteOperatorHandler;
import com.msm.core.dynamicquery.operator.impl.NeOperatorHandler;
import com.msm.core.dynamicquery.operator.impl.NotInOperatorHandler;
import com.msm.core.exceptions.CommonErrors;
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
        register(FilterOperator.CONTAINS, new ContainsOperatorHandler());
        register(FilterOperator.CONTAINS_ONE_OF, new ContainsOneOfOperatorHandler());
    }

    public static void register(FilterOperator operator, OperatorHandler handler) {
        HANDLERS.put(operator, handler);
    }

    public static OperatorHandler get(FilterOperator operator) {
        OperatorHandler handler = HANDLERS.get(operator);
        if(Objects.nonNull(handler)){
            return handler;
        }

        throw CommonErrors.unsupported(operator.name(), "Unsupported operator: " + operator);
    }

}