package com.msm.core.hook.context;

import com.msm.core.hook.common.ExecutionContext;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionRequest implements ExecutionContext {

    private String objectName;
    private String action;
    private Object objectId;
    private Map<String, Object> payload;
    @Builder.Default
    private Map<ContextKey<?>, Object> contextData = new HashMap<>();

    @Override
    public Map<ContextKey<?>, Object> getContextData() {
        return contextData;
    }
}
