package com.msm.core.hook;

import java.util.Map;


public class DynamicRecordServiceImpl extends AbstractDynamicRecordService {
    protected DynamicRecordServiceImpl(DefaultHookEngine defaultHookEngine) {
        super(defaultHookEngine);
    }

    @Override
    public <T> T saveObject(String objectName, Map<String, Object> payload) {

        return null;
    }

    @Override
    public void updateObject(String objectName, Map<String, Object> payload) {

    }

    @Override
    public void deleteObject(String objectName, Map<String, Object> payload) {

    }
}
