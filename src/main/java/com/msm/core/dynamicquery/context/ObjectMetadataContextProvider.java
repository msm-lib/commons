package com.msm.core.dynamicquery.context;

import com.msm.core.metadata.ObjectMetadata;

import java.util.Optional;

public interface ObjectMetadataContextProvider {
    Optional<ObjectMetadata> getObjectMetadata(String objectName);
}
