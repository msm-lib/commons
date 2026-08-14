package com.msm.core.metadata.typesafe;

public interface ValueResolver<T> {
    T resolve(DataRecord record);
}