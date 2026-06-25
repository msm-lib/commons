package com.msm.core.security.context;

import com.msm.core.commons.Utils;
import com.msm.core.security.IdentifiableCode;
import com.msm.core.security.context.impl.DefaultAuthorizationContext;
import com.msm.core.security.enums.AccessScope;
import com.msm.core.security.enums.PermissionAction;

import java.util.Set;

public interface AuthorizationContext {
    Set<AccessScope> getScope(String objectName, IdentifiableCode action);
    boolean hasPermission(String objectName, IdentifiableCode action);



    default boolean canView(String objectName) {
        return hasPermission(objectName, PermissionAction.VIEW);
    }

    default boolean canCreate(String objectName) {
        return hasPermission(objectName, PermissionAction.CREATE);
    }

    default boolean canUpdate(String objectName) {
        return hasPermission(objectName, PermissionAction.UPDATE);
    }

    default boolean canDelete(String objectName) {
        return hasPermission(objectName, PermissionAction.DELETE);
    }

    AuthorizationContext DEFAULT_AUTHORIZATION_CONTEXT = new DefaultAuthorizationContext(Utils.CL.newHashMap());
}
