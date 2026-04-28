package com.msm.core.strategy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractStrategyRegistry<T> implements StrategyResolver<T> {

    protected final Map<String, T> cache = new ConcurrentHashMap<>();
    protected final T defaultStrategy;

    protected AbstractStrategyRegistry(List<T> strategies, T defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
        strategies.forEach(this::register);
    }

    protected abstract String supportObjectType(T strategy);

    private void register(T strategy) {
        cache.put(supportObjectType(strategy), strategy);
    }

    @Override
    public T resolve(String objectName) {
        return cache.getOrDefault(objectName, defaultStrategy);
    }
}