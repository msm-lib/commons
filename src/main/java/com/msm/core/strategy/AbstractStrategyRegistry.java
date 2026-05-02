package com.msm.core.strategy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractStrategyRegistry<O> implements StrategyResolver<String, O> {

    protected final Map<String, O> cache = new ConcurrentHashMap<>();
    protected final O defaultStrategy;

    protected AbstractStrategyRegistry(List<O> strategies, O defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
        strategies.forEach(this::register);
    }

    protected abstract String supportObjectType(O strategy);

    private void register(O strategy) {
        cache.put(supportObjectType(strategy), strategy);
    }

    @Override
    public O resolve(String objectName) {
        return cache.getOrDefault(objectName, defaultStrategy);
    }
}