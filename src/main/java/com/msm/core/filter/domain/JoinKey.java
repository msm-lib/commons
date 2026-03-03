package com.msm.core.filter.domain;

public record JoinKey(Class<?> sourceType, String attribute) {
    @Override
    public String toString() {
        return sourceType.getSimpleName() + "." + attribute;
    }
}
