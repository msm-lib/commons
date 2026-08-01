package com.msm.core.filter;


import com.msm.core.filter.domain.AggregateRequest;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.PathBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AggregateBuilder {

    public static List<Expression<?>> build(
            List<AggregateRequest> req,
            PathBuilder<?> root
    ) {
        List<Expression<?>> exps = new ArrayList<>();

        for (AggregateRequest a : req) {
            switch (a.getType()) {
                case COUNT ->
                        exps.add(root.get(a.getField()).count());
                case SUM ->
                        exps.add(
                                root.getNumber(a.getField(), BigDecimal.class).sumBigDecimal()
                        );
            }
        }
        return exps;
    }
}
