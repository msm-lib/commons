package com.msm.core.strategy;

public interface StrategyResolver<X> {
    X resolve(String objectName);
}
