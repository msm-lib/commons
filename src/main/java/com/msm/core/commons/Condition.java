package com.msm.core.commons;

public interface Condition<I> {
    boolean matches(I ctx);
}
