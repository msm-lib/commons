package com.msm.core.strategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractStrategyRegistry<T, O extends TypedStrategy<T>> implements StrategyResolver<T, O> {

    protected final Map<T, O> strategies;
    protected final O defaultStrategy;

    protected AbstractStrategyRegistry(List<O> strategies, O defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
        this.strategies = strategies
                .stream()
                .collect(Collectors.toUnmodifiableMap(
                        TypedStrategy::support,
                        Function.identity()
                ));
    }

    protected AbstractStrategyRegistry(List<O> strategies) {
        this(strategies, null);
    }

    @Override
    public O resolve(T type) {

        O strategy = strategies.getOrDefault(type, defaultStrategy);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for: " + type);
        }

        return strategy;
    }
}