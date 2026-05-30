package com.msm.core.dynamicquery.locking;

import com.msm.core.exceptions.CommonErrors;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.Map;

public class OptimisticLocks {
    public static Condition apply(ObjectMetadata meta, Map<Field<?>, Object> fields) {
        Attribute versionAttr = meta.getVersionAttribute();
        if (versionAttr != null) {
            Field<?> versionField = versionAttr.getField();
            Object versionValueObject = fields.get(versionField);
            if(versionValueObject == null) {
                throw CommonErrors.required("version", "Version must not be null");
            }
            if(versionValueObject instanceof Number currentVersion) {
                fields.put(versionField, currentVersion.longValue() + 1);
                return ((Field<Object>) versionField).eq(currentVersion.longValue());
            }
        }
        return DSL.noCondition();
    }
}
