package com.msm.core.security;

import com.msm.core.dynamicquery.ObjectMetadataFactory;
import com.msm.core.metadata.ObjectMetadata;
import com.msm.core.metadata.ObjectRelation;
import com.msm.core.metadata.RelationalTypeEnum;

import java.util.Optional;

public final class ObjectAccessScopeResolver {
    private ObjectAccessScopeResolver() {}
    public static String resolveObjectAccessScope(ObjectMetadata metadata) {

        ObjectRelation relation = metadata.getObjectRelation();
        if (relation == null) {
            return metadata.getName();
        }

        if (RelationalTypeEnum.CHILD_OF.equals(relation.getRelationType())) {
            return relation.getTargetObject();
        }

        return metadata.getName();
    }

    public static String resolveObjectAccessScope(String objectName) {
        Optional<ObjectMetadata> objectMetadataOptional = ObjectMetadataFactory.getObjectMetadata(objectName);
        if (objectMetadataOptional.isPresent()) {
            return resolveObjectAccessScope(objectMetadataOptional.get());
        }
        return objectName;
    }
}
