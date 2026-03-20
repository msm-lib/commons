package com.msm.core.hook;

import com.querydsl.core.types.Predicate;

import java.util.Map;

public interface DynamicRecordService {

    <T> T save(String objectName, Map<String, Object> payload) throws Exception;
    void update(String objectName, Object id, Map<String, Object> payload) throws Exception;
    void delete(String objectName, Object id) throws Exception;
}