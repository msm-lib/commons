package com.msm.core.security.context.impl;

import com.msm.core.security.context.DataScopeContext;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class DefaultDataScopeContext implements DataScopeContext {

    private final Set<UUID> teamIds;
    private final Set<UUID> orgIds;
    private final Set<UUID> parentChildOrgIds;
    private final UUID ownerId;

    public DefaultDataScopeContext(Set<UUID> teamIds, Set<UUID> orgIds, Set<UUID> parentChildOrgIds, UUID ownerId) {
        this.teamIds = teamIds;
        this.orgIds = orgIds;
        this.parentChildOrgIds = parentChildOrgIds;
        this.ownerId = ownerId;
    }

    public DefaultDataScopeContext() {
        this.teamIds = Set.of();
        this.orgIds = Set.of();
        this.parentChildOrgIds = Set.of();
        this.ownerId = null;
    }
}
