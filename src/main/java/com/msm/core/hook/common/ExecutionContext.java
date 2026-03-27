package com.msm.core.hook.common;


import com.msm.core.hook.context.ContextKey;

import java.util.Map;
import java.util.Optional;

public interface ExecutionContext extends ExecutionMetadata {

    Map<String, Object> getPayload();

    default  <T> void addContextKey(ContextKey<T> key, T value) {
        getContextData().put(key, value);
    }

    default  <T> T getContextKey(ContextKey<T> key) {
        return (T) getContextData().get(key);
    }

    default  <T> Optional<T> getOptional(ContextKey<T> key) {
        return Optional.ofNullable(getContextKey(key));
    }
}