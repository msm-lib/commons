package com.msm.core.action.context;

import com.msm.core.commons.Constants;
import com.msm.core.security.RequestContextHolder;
import com.msm.core.security.context.RequestContext;

import java.util.Map;
import java.util.Optional;

public interface ExecutionContext<I> {
    boolean isInternalExecution();
    default String getResource() {return Constants.GENERIC_RESOURCE_NAME;}
    I getPayload();
    String getAction();
    Map<ContextKey<?>, Object> getContextKey();
    default  <T> void addContextKey(ContextKey<T> key, T value) {
        getContextKey().put(key, value);
    }
    default  <T> T getContextKey(ContextKey<T> key) {
        return (T) getContextKey().get(key);
    }

    default  <T> Optional<T> getOptional(ContextKey<T> key) {
        return Optional.ofNullable(getContextKey(key));
    }

    default RequestContext getRequestContext() {
        return RequestContextHolder.getRequestContext();
    }
}