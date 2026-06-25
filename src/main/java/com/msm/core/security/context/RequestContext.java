package com.msm.core.security.context;

import java.util.UUID;

public interface RequestContext {
    UUID getUserId();
    String getUsername();
    boolean isSupperAdmin();
    AuthorizationContext getAuthorization();
    DataScopeContext getDataScopeContext();
}
