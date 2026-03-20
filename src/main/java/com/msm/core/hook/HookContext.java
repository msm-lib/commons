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

    private String objectApi;
    private String action;
    private HookPhase phase;
    private UUID recordId;
    private Object oldRecord;
    private Map<String, Object> payload;

    private Map<String, Object> additionalData;

    public HookContext forAfterCommit() {
        return new HookContext(objectApi, action, HookPhase.AFTER_EVENT, recordId, oldRecord, new HashMap<>(payload), new HashMap<>());
    }

    public static HookContext ofDefault(String objectApi, String action, Map<String, Object> payload) {
        return new HookContext(objectApi, action, HookPhase.BEFORE_EVENT, null, null, payload, new HashMap<>());
    }

    public void nextPhase(HookPhase nextPPhase) {
        this.phase = nextPPhase;
    }
}