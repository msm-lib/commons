package com.msm.core.security.context;

import java.util.UUID;

public interface RequestContext {
    String getTenantCode();
    UUID getUserId();
    String getUsername();
    boolean isSupperAdmin();
    AuthorizationContext getAuthorization();
    DataScopeContext getDataScopeContext();
}
