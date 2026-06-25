package com.msm.core.security;

import com.msm.core.commons.Utils;
import com.msm.core.dynamicquery.ObjectMetadataFactory;
import com.msm.core.metadata.ObjectMetadata;
import com.msm.core.metadata.ObjectRelation;
import com.msm.core.security.context.AuthorizationContext;
import com.msm.core.security.context.RequestContext;
import com.msm.core.security.enums.AccessScope;
import com.msm.core.security.enums.PermissionAction;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.Set;

@RequiredArgsConstructor
public class SecurityConditionProvider {
    private final DataScopeResolver dataScopeResolver;

    public Condition buildDataScopeCondition(ObjectMetadata meta, PermissionAction permissionAction) {
        RequestContext requestContext = RequestContextHolder.getRequestContext();
        AuthorizationContext auth = requestContext.getAuthorization();
        Set<AccessScope> accessScopes = auth.getScope(meta.getName(), permissionAction);

        String objectName = ObjectAccessScopeResolver.resolveObjectAccessScope(meta);
        if (Utils.STR.equalIgnoreCase(objectName, meta.getName())) {
            return dataScopeResolver.resolve(meta, accessScopes, requestContext);
        }

        ObjectRelation objectRelation = meta.getObjectRelation();
        ObjectMetadata objectMetadata = ObjectMetadataFactory.getObjectMetadataByName(objectName);
        Field<Object> field0 = (Field<Object>) objectMetadata.getIdAttribute().getField();
        Field<Object> field = (Field<Object>) meta.getAttributeByName(objectRelation.getForeignKeyAttribute()).getField();
        return DSL.exists(
                DSL.selectOne()
                        .from(objectMetadata.getTable())
                        .where(field0.eq(field))
                        .and(dataScopeResolver.resolve(meta, accessScopes, requestContext))
        );
    }

    public Condition buildViewDataScopeCondition(ObjectMetadata meta) {
        return buildDataScopeCondition(meta, PermissionAction.VIEW);
    }

    public Condition buildCreateDataScopeCondition(ObjectMetadata meta) {
        return buildDataScopeCondition(meta, PermissionAction.CREATE);
    }

    public Condition buildUpdateDataScopeCondition(ObjectMetadata meta) {
        return buildDataScopeCondition(meta, PermissionAction.UPDATE);
    }

    public Condition buildDeleteDataScopeCondition(ObjectMetadata meta) {
        return buildDataScopeCondition(meta, PermissionAction.DELETE);
    }

}
