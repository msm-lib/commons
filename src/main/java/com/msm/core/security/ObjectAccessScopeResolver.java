package com.msm.core.security;

import com.msm.core.dynamicquery.ObjectMetadataFactory;
import com.msm.core.metadata.ObjectMetadata;
import com.msm.core.metadata.ObjectRelation;
import com.msm.core.metadata.RelationalTypeEnum;

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
        ObjectMetadata objectMetadata = ObjectMetadataFactory.getObjectMetadataByName(objectName);
        return resolveObjectAccessScope(objectMetadata);
    }
}
