package com.msm.core.commons;

public interface ObjectValueConverter<F> {
    <T> T convert(String objectName, F from, Object optionalParam);
}
