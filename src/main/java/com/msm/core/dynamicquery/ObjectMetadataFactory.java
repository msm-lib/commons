package com.msm.core.dynamicquery;

import com.msm.core.exceptions.Errors;
import com.msm.core.metadata.ObjectMetadata;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectMetadataFactory {

    private static final Map<String, ObjectMetadata> CACHE_TABLE = new ConcurrentHashMap<>();

    public static void registerObjectMetadata(ObjectMetadata objectMetadata) {
        CACHE_TABLE.put(objectMetadata.getName().toLowerCase(), objectMetadata);
    }

    public static ObjectMetadata getObjectMetadata(String objectName) {
        ObjectMetadata objectMetadata = CACHE_TABLE.get(objectName);
        if (objectMetadata == null) {
            throw Errors.unsupported("Unsupported object: " + objectName);
        }
        return objectMetadata;
    }
}
