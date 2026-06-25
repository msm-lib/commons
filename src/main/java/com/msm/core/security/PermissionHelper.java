package com.msm.core.security;

import com.msm.core.commons.Utils;
import com.msm.core.security.context.AuthorizationContext;
import com.msm.core.security.context.DataScopeContext;
import com.msm.core.security.context.impl.DefaultAuthorizationContext;
import com.msm.core.security.context.impl.DefaultDataScopeContext;
import com.msm.core.security.enums.AccessScope;
import com.msm.core.security.enums.PermissionAction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class PermissionHelper {

    private PermissionHelper() {
    }

    public static AuthorizationContext parseAuthorizationContext(Map<String, Map<String, Set<String>>> rawPermissions) {

        Map<String, Map<IdentifiableCode, Set<AccessScope>>> result = new HashMap<>();
        Utils.CL.emptyIfNull(rawPermissions).forEach((objectName, actions) -> {
            Map<IdentifiableCode, Set<AccessScope>> actionMap = new HashMap<>();
            actions.forEach((actionCode, scopeCodes) -> {
                IdentifiableCode action = PermissionAction.fromCode(actionCode);
                Set<AccessScope> scopeList = scopeCodes.stream().map(AccessScope::fromCode).collect(Collectors.toSet());
                actionMap.put(action, scopeList);
            });
            result.put(Utils.STR.lowCase(objectName), actionMap);
        });

        return new DefaultAuthorizationContext(result);
    }

    public static DataScopeContext createDataScopeContext(
            Set<UUID> teamIds,
            Set<UUID> orgIds,
            Set<UUID> parentChildOrgIds,
            UUID ownerId
    ) {

        return new DefaultDataScopeContext(teamIds, orgIds, parentChildOrgIds, ownerId);
    }


}
