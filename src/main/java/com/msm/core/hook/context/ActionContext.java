package com.msm.core.hook.context;

import com.msm.core.hook.common.ExecutionContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionContext<I> implements ExecutionContext<I> {

    @Builder.Default
    private String resource = "";
    private String action;
    private Object objectId;
    private Object result;
    private Object additionalParameter;
    private I payload;
    @Builder.Default
    private boolean disableHookEvent = false;
    @Builder.Default
    private Map<ContextKey<?>, Object> contextKey = new HashMap<>();

    @Override
    public Map<ContextKey<?>, Object> getContextKey() {
        return contextKey;
    }
}
