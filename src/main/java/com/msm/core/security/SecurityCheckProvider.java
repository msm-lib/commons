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
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class SecurityCheckProvider {
    private final DSLContext dsl;
    private final DataScopeResolver dataScopeResolver;

    public boolean checkDataScope(ObjectMetadata currentMetadata, Map<String, Object> dataContext, PermissionAction permissionAction) {
        RequestContext requestContext = RequestContextHolder.getRequestContext();
        AuthorizationContext auth = requestContext.getAuthorization();
        Set<AccessScope> accessScopes = auth.getScope(currentMetadata.getName(), permissionAction);

        String securityObjectName = ObjectAccessScopeResolver.resolveObjectAccessScope(currentMetadata);
        if (Utils.STR.equalIgnoreCase(securityObjectName, currentMetadata.getName())) {
            return dataScopeResolver.resolve(ObjectMetadataFactory.getObjectMetadataByName(securityObjectName), accessScopes, requestContext, dataContext);
        }
        ObjectRelation objectRelation = currentMetadata.getObjectRelation();
        ObjectMetadata securityObjectMetadata = ObjectMetadataFactory.getObjectMetadataByName(securityObjectName);
        Field<Object> fieldTargetAttribute = (Field<Object>) securityObjectMetadata.getAttributeByName(objectRelation.getTargetAttribute()).getField();
        Object foreignKeyData = dataContext.get(objectRelation.getForeignKeyAttribute());

        return dsl.fetchExists(
                DSL.selectOne()
                        .from(securityObjectMetadata.getTable())
                        .where(fieldTargetAttribute.eq(foreignKeyData))
                        .and(dataScopeResolver.resolve(currentMetadata, accessScopes, requestContext))
        );
    }

    public boolean checkDataScope(String objectName, Map<String, Object> dataContext, PermissionAction permissionAction) {
        ObjectMetadata currentMetadata = ObjectMetadataFactory.getObjectMetadataByName(objectName);
        RequestContext requestContext = RequestContextHolder.getRequestContext();
        AuthorizationContext auth = requestContext.getAuthorization();


        String securityObjectName = ObjectAccessScopeResolver.resolveObjectAccessScope(objectName);
        Set<AccessScope> accessScopes = auth.getScope(securityObjectName, permissionAction);
        if (Utils.STR.equalIgnoreCase(objectName,securityObjectName)) {
            return dataScopeResolver.resolve(ObjectMetadataFactory.getObjectMetadataByName(securityObjectName), accessScopes, requestContext, dataContext);
        }

        ObjectRelation objectRelation = currentMetadata.getObjectRelation();
        ObjectMetadata securityObjectMetadata = ObjectMetadataFactory.getObjectMetadataByName(securityObjectName);
        Field<Object> fieldTargetAttribute = (Field<Object>) securityObjectMetadata.getAttributeByName(objectRelation.getTargetAttribute()).getField();
        Object foreignKeyData = dataContext.get(objectRelation.getForeignKeyAttribute());

        return dsl.fetchExists(
                DSL.selectOne()
                        .from(securityObjectMetadata.getTable())
                        .where(fieldTargetAttribute.eq(foreignKeyData))
                        .and(dataScopeResolver.resolve(currentMetadata, accessScopes, requestContext))
        );
    }


    public boolean checkDataScope(String objectName, List<Map<String, Object>> dataContexts, PermissionAction permissionAction) {
        ObjectMetadata currentMetadata = ObjectMetadataFactory.getObjectMetadataByName(objectName);
        RequestContext requestContext = RequestContextHolder.getRequestContext();
        AuthorizationContext auth = requestContext.getAuthorization();


        String securityObjectName = ObjectAccessScopeResolver.resolveObjectAccessScope(objectName);
        Set<AccessScope> accessScopes = auth.getScope(securityObjectName, permissionAction);
        if (Utils.STR.equalIgnoreCase(objectName,securityObjectName)) {
            return dataContexts.stream().allMatch(dataContext -> dataScopeResolver.resolve(ObjectMetadataFactory.getObjectMetadataByName(securityObjectName), accessScopes, requestContext, dataContext));
        }

        ObjectRelation objectRelation = currentMetadata.getObjectRelation();
        ObjectMetadata securityObjectMetadata = ObjectMetadataFactory.getObjectMetadataByName(securityObjectName);
        Field<Object> fieldTargetAttribute = (Field<Object>) securityObjectMetadata.getAttributeByName(objectRelation.getTargetAttribute()).getField();

        Map<Object, Map<String, Object>> dataContextGroupBy = Utils.CL.toMap(dataContexts, objectKey -> objectKey.get(objectRelation.getForeignKeyAttribute()), objValue -> objValue);

        if(dataContextGroupBy.size() > 1) {
            return false;
        }

        Object foreignKeyData = dataContexts.getFirst().get(objectRelation.getForeignKeyAttribute());

        return dsl.fetchExists(
                DSL.selectOne()
                        .from(securityObjectMetadata.getTable())
                        .where(fieldTargetAttribute.eq(foreignKeyData))
                        .and(dataScopeResolver.resolve(currentMetadata, accessScopes, requestContext))
        );
    }

}
