package com.msm.core.dynamicquery.context;

import com.msm.core.dynamicquery.ObjectMetadataFactory;
import com.msm.core.metadata.ObjectMetadata;
import lombok.Setter;

import java.util.Optional;

public class ObjectMetadataContextHolder {
    @Setter
    private static ObjectMetadataContextProvider objectMetadataContextProvider = ObjectMetadataFactory::getObjectMetadata;

    public static Optional<ObjectMetadata> getObjectMetadata(String name) {
        return objectMetadataContextProvider.getObjectMetadata(name);
    }
}
