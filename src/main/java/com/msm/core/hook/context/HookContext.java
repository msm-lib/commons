package com.msm.core.hook.context;

import com.msm.core.commons.Constants;
import com.msm.core.hook.HookPhase;
import com.msm.core.hook.ObjectHookMetaDataFactory;
import com.msm.core.hook.common.ExecutionContext;
import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HookContext implements ExecutionContext {

    private String objectName;
    private String action;
    private HookPhase phase;
    private UUID recordId;
    private Object currentRecord;
    private Map<String, Object> payload;
    private Map<String, Object> additionalData;

//    public HookContext forAfterCommit() {
//        return new HookContext(objectName,
//                Utils.STR.defaultIfBlank(objectRecordType, () -> Constants.GENERIC_OBJECT_RECORD_TYPE),
//                action,
//                HookPhase.AFTER_EVENT,
//                recordId,
//                currentRecord,
//                new HashMap<>(payload),
//                new HashMap<>());
//    }
//
//    public static HookContext ofDefault(String objectName, String objectRecordType, String action, Map<String, Object> payload) {
//        return new HookContext(objectName,
//                Utils.STR.defaultIfBlank(objectRecordType, () -> Constants.GENERIC_OBJECT_RECORD_TYPE),
//                action,
//                HookPhase.BEFORE_EVENT,
//                null,
//                null,
//                payload,
//                new HashMap<>());
//    }
//
//    public static HookContext ofDefault(String objectName, String action, Map<String, Object> payload) {
//        return new HookContext(objectName,
//                Constants.GENERIC_OBJECT_RECORD_TYPE,
//                action,
//                HookPhase.BEFORE_EVENT,
//                null,
//                null,
//                payload,
//                new HashMap<>());
//    }

    public void nextPhase(HookPhase nextPPhase) {
        this.phase = nextPPhase;
    }

    protected String hookMetaDataKey() {
        return ObjectHookMetaDataFactory.buildKey(getObjectName(), getAction(), getPhase().name());
    }

    protected String defaultHookMetaDataKey() {
        return ObjectHookMetaDataFactory.buildKey(Constants.GENERIC_OBJECT_HOOK_NAME, getAction(), getPhase().name());
    }



    private Map<ContextKey<?>, Object> contextData = new LinkedHashMap<>();

    public <T> void put(ContextKey<T> key, T value) {
        contextData.put(key, value);
    }

    public <T> T get(ContextKey<T> key) {
        return (T) contextData.get(key);
    }

//    public <T> Optional<T> getOptional(ContextKey<T> key) {
//        return Optional.ofNullable(get(key));
//    }

    @Override
    public Map<ContextKey<?>, Object> getContextData() {
        return contextData;
    }
}