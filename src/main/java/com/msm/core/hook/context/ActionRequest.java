package com.msm.core.hook.context;

import com.msm.core.commons.Converter;
import com.msm.core.hook.common.ExecutionContext;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionRequest<T> implements ExecutionContext {

    @Builder.Default
    private String objectName = "";
    private String action;
    private Object objectId;
    private Object additionalParameter;
    private T payload;
    @Builder.Default
    private Converter<ActionRequest<T>, HookContext<T>> hookContextConvertStrategy = new DefaultHookContextConvertStrategy<>();
    @Builder.Default
    private Map<ContextKey<?>, Object> contextKey = new HashMap<>();

    @Override
    public Map<ContextKey<?>, Object> getContextKey() {
        return contextKey;
    }
}
