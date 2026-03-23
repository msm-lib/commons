package com.msm.core.hook;

import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;
import com.msm.core.filter.AdvancedFilterService;
import com.msm.core.filter.domain.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public abstract class AbstractDynamicRecordService implements DynamicRecordService {

    private final HookEngine hookEngine;
    private final AdvancedFilterService filterService;

    @Override
    public <T> T save(String objectName, Map<String, Object> payload) {
        HookContext hookContext = HookContext.ofDefault(objectName, "create", payload);
        hookEngine.execute(objectName, hookContext);
        T returnObject = saveObject(objectName, payload);
        hookContext.nextPhase(HookPhase.BEFORE_EVENT);
        hookContext.setCurrentRecord(returnObject);
        hookEngine.execute(objectName, hookContext);
        hookContext.nextPhase(HookPhase.AFTER_EVENT);
        hookEngine.execute(objectName, hookContext);
        return returnObject;
    }

    @Override
    public void update(String objectName, Object id, Map<String, Object> payload) {
        Object currentObject = getObjectById(objectName, id);
        HookContext hookContext = HookContext.ofDefault(objectName, "update", payload);
        hookContext.setCurrentRecord(currentObject);
        hookEngine.execute(objectName, hookContext);
        updateObject(objectName, id, currentObject, payload);
        hookContext.nextPhase(HookPhase.BEFORE_EVENT);
        hookEngine.execute(objectName, hookContext);
        hookContext.nextPhase(HookPhase.AFTER_EVENT);
        hookEngine.execute(objectName, hookContext);
    }

    @Override
    public void delete(String objectName, Object id) {
        Object currentObject = getObjectById(objectName, id);
        Map<String, Object> payload = Utils.CL.newHashMap(Constants.OBJECT_PK, id);
        HookContext hookContext = HookContext.ofDefault(objectName, "delete", payload);
        hookContext.setCurrentRecord(currentObject);
        hookEngine.execute(objectName, hookContext);
        deleteObject(objectName, id);
        hookContext.nextPhase(HookPhase.BEFORE_EVENT);
        hookEngine.execute(objectName, hookContext);
        hookContext.nextPhase(HookPhase.AFTER_EVENT);
        hookEngine.execute(objectName, hookContext);
    }

    private <T> T getObjectById(String objectName, Object valId) {
        ObjectFilterRequest objectFilterRequest = ObjectFilterRequest
                .builder()
                .objectInfo(ObjectFilterRequest.ObjectInfo.of(objectName))
                .filters(FilterGroup
                        .builder()
                        .operator(LogicalOperator.AND)
                        .conditions(List.of(FilterCondition.create(Constants.OBJECT_PK, FilterOperator.EQUALS, valId)))
                        .build()
                ).build();
        PageResponse<Map<String, Object>> result = filterService.filter(objectFilterRequest);
        Class<?> returnClass = filterService.getEntityClassFactory().resolve(objectName);
        return Utils.CL.isNotEmpty(result.getContents()) ? (T) Utils.O.toObject(result.getContents().getFirst(), returnClass) : null;
    }

    public abstract <X> X saveObject(String objectName, Map<String, Object> payload);
    public abstract <X> void updateObject(String objectName, Object id, X oldData, Map<String, Object> payload);
    public abstract <X> void deleteObject(String objectName, Object id);
}
