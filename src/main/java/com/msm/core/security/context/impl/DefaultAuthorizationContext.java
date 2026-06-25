package com.msm.core.security.context.impl;

import com.msm.core.commons.Utils;
import com.msm.core.security.IdentifiableCode;
import com.msm.core.security.ObjectAccessScopeResolver;
import com.msm.core.security.context.AuthorizationContext;
import com.msm.core.security.enums.AccessScope;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class DefaultAuthorizationContext implements AuthorizationContext {

    private final Map<String, Map<IdentifiableCode, Set<AccessScope>>> permissions;

    @Override
    public Set<AccessScope> getScope(String objectName, IdentifiableCode action) {

        return permissions
                .getOrDefault(ObjectAccessScopeResolver.resolveObjectAccessScope(objectName), Map.of())
                .getOrDefault(action, Set.of());
    }

    @Override
    public boolean hasPermission(String objectName, IdentifiableCode action) {
        return Utils.CL.isNotEmpty(getScope(ObjectAccessScopeResolver.resolveObjectAccessScope(objectName), action));
    }
}