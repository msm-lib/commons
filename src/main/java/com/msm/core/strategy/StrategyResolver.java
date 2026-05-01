package com.msm.core.strategy;

public interface StrategyResolver<I, O> {
    O resolve(I input);
}
