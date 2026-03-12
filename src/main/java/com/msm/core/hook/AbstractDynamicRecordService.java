package com.msm.core.hook;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public abstract class AbstractDynamicRecordService implements DynamicRecordService {

    private final HookEngine hookEngine;

    @Override
    public <T> T save(String objectName, Map<String, Object> payload) throws Exception {
        HookContext hookContext = HookContext.ofDefault(objectName, payload);
        hookEngine.execute(objectName, hookContext);
        T returnObject = saveObject(objectName, payload);
        hookContext.nextPhase(HookPhase.BEFORE_EVENT);
//        hookContext.setRecordId((UUID) Utils.O.getProperty(returnObject, "id"));
        hookEngine.execute(objectName, hookContext);
        hookContext.nextPhase(HookPhase.AFTER_EVENT);
        hookEngine.execute(objectName, hookContext);
        return returnObject;
    }

    @Override
    public void update(String objectName, Map<String, Object> payload) throws Exception {
        HookContext hookContext = HookContext.ofDefault(objectName, payload);
        hookEngine.execute(objectName, hookContext);

        updateObject(objectName, payload);

        hookContext.nextPhase(HookPhase.BEFORE_EVENT);
//        hookContext.setRecordId(UUID.fromString(payload.get("id").toString()));
        hookEngine.execute(objectName, hookContext);
        hookContext.nextPhase(HookPhase.AFTER_EVENT);
        hookEngine.execute(objectName, hookContext);
    }

    @Override
    public void delete(String objectName, Map<String, Object> payload) throws Exception {
        HookContext hookContext = HookContext.ofDefault(objectName, payload);
        hookEngine.execute(objectName, hookContext);
        deleteObject(objectName, payload);
        hookContext.nextPhase(HookPhase.BEFORE_EVENT);
//        hookContext.setRecordId(UUID.fromString(payload.get("id").toString()));
        hookEngine.execute(objectName, hookContext);
        hookContext.nextPhase(HookPhase.AFTER_EVENT);
        hookEngine.execute(objectName, hookContext);
    }


    public abstract <T> T saveObject(String objectName, Map<String, Object> payload);
    public abstract void updateObject(String objectName, Map<String, Object> payload);
    public abstract void deleteObject(String objectName, Map<String, Object> payload);

}
