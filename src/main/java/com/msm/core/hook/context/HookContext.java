package com.msm.core.hook.context;

import com.msm.core.hook.HookPhase;
import com.msm.core.hook.common.ExecutionContext;
import lombok.*;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HookContext<T> implements ExecutionContext {

    private String objectName;
    private String action;
    private HookPhase phase;
    private Object objectId;
    private Object currentRecord;
    private T payload;
    private Object additionalData;
    private Throwable error;

    public void nextPhase(HookPhase nextPhase) {
        this.phase = nextPhase;
    }

    private Map<ContextKey<?>, Object> contextKey = new LinkedHashMap<>();

    @Override
    public Map<ContextKey<?>, Object> getContextKey() {
        return contextKey;
    }
}