package com.msm.core.hook;

import java.util.Map;

public interface DynamicRecordService {

    <T> T save(String objectName, Map<String, Object> payload) throws Exception;
    void update(String objectName, Map<String, Object> payload) throws Exception;
    void delete(String objectName, Map<String, Object> payload) throws Exception;
}