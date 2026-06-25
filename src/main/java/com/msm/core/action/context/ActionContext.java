package com.msm.core.action.context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.msm.core.commons.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
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

    @SuppressWarnings("unchecked")
    public Map<String, Object> getResultAsMap() {
        if(result instanceof Map){
            return (Map<String, Object>) result;
        }
        return getResultAs(new TypeReference<>() {});
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getResultAsListMap() {
        if(result instanceof List<?>){
            return (List<Map<String, Object>>) result;
        }
        return getResultAs(new TypeReference<>() {});
    }

    public <X> X getResultAs(Class<X> clazz) {
        return Utils.O.convertToType(result, clazz);
    }

    public <X> X getResultAs(TypeReference<X> typeReference) {
        return Utils.O.convertToType(result, typeReference);
    }

    public <X> X getObjectIdAs(Class<X> clazz) {
        return Utils.O.convertToType(objectId, clazz);
    }

    public <X> X getObjectIdAs(TypeReference<X> typeReference) {
        return Utils.O.convertToType(objectId, typeReference);
    }

    public <X> X getAdditionalParameterAs(Class<X> clazz) {
        return Utils.O.convertToType(additionalParameter, clazz);
    }

    public <X> X getAdditionalParameterAs(TypeReference<X> clazz) {
        return Utils.O.convertToType(additionalParameter, clazz);
    }
}
