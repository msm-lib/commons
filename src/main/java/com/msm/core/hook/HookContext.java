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
    private UUID recordId;
    private Map<String, Object> payload;
    private HookPhase phase;
    private Map<String, Object> additionalData;

    public HookContext forAfterCommit() {
        return new HookContext(objectApi, recordId, new HashMap<>(payload), HookPhase.AFTER_EVENT, new HashMap<>());
    }

    public static HookContext ofDefault(String objectApi, Map<String, Object> payload) {
        return new HookContext(objectApi, null, payload, HookPhase.BEFORE_EVENT, new HashMap<>());
    }

    public void nextPhase(HookPhase nextPPhase) {
        this.phase = nextPPhase;
    }
}