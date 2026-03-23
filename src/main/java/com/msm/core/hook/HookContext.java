package com.msm.core.hook;

import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class HookContext {

    private String objectName;
    private String action;
    private HookPhase phase;
    private UUID recordId;
    private Object currentRecord;
    private Map<String, Object> payload;
    private Map<String, Object> additionalData;

    public HookContext forAfterCommit() {
        return new HookContext(objectName, action, HookPhase.AFTER_EVENT, recordId, currentRecord, new HashMap<>(payload), new HashMap<>());
    }

    public static HookContext ofDefault(String objectName, String action, Map<String, Object> payload) {
        return new HookContext(objectName, action, HookPhase.BEFORE_EVENT, null, null, payload, new HashMap<>());
    }

    public void nextPhase(HookPhase nextPPhase) {
        this.phase = nextPPhase;
    }
}