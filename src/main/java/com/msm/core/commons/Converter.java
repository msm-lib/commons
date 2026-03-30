package com.msm.core.commons;

@FunctionalInterface
public interface Converter<IN, OUT> {
    OUT convert(IN in);
}
