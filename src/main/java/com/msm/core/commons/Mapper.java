package com.msm.core.commons;

public interface Mapper<F, T> {
    T map(F row);
}