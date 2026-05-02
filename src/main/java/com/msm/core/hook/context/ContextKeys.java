package com.msm.core.hook.context;

import java.util.Map;

public class ContextKeys {
    public static final ContextKey<String> HOOK_PREFIX = ContextKey.of("hookPrefix");
    public static final ContextKey<String> RESOURCE_NAME = ContextKey.of("resourceName");
    public static final ContextKey<String> ACTION_PREFIX = ContextKey.of("actionPrefix");
    public static final ContextKey<String> ACTION = ContextKey.of("action");
    public static final ContextKey<String> PHASE = ContextKey.of("phase");
    public static final ContextKey<Map<String, Object>> PAYLOAD = ContextKey.of("payload");
    public static final ContextKey<Object> RECORD_TYPE = ContextKey.of("recordType");
    public static final ContextKey<Object> OBJECT_TYPE = ContextKey.of("objectType");
    public static final ContextKey<String> USER_ID = ContextKey.of("userId");
    public static final ContextKey<String> REQUEST_ID = ContextKey.of("requestId");
}
