package com.msm.core.hook;

import java.util.Map;

public interface DynamicRecordService {

    <T> T save(String objectName, Map<String, Object> payload);
    void update(String objectName, Map<String, Object> payload);
    void delete(String objectName, Map<String, Object> payload);
}