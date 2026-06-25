package com.msm.core.security.context;

import java.util.UUID;

public interface RequestContext {
    UUID getUserId();
    String getUsername();
    AuthorizationContext getAuthorization();
    DataScopeContext getDataScopeContext();
}
