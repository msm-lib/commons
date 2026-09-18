package com.msm.core.security.context;

import com.msm.core.security.model.Team;

import java.util.UUID;

public interface RequestContext {
    String getTenantCode();
    UUID getUserId();
    String getUsername();
    Team getTeam();
    boolean isSupperAdmin();
    AuthorizationContext getAuthorization();
    DataScopeContext getDataScopeContext();
}
