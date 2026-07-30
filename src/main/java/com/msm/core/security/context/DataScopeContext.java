package com.msm.core.security.context;

import com.msm.core.security.context.impl.DefaultDataScopeContext;

import java.util.Set;
import java.util.UUID;

public interface DataScopeContext {
    Set<UUID> getOrgIds();
    Set<UUID> getParentChildOrgIds();
    Set<UUID> getParentChildParentOrgIds();
    Set<UUID> getTeamIds();
    UUID getOwnerId();

    DataScopeContext DEFAULT_DATA_SCOPE_CONTEXT = new DefaultDataScopeContext();
}
