package com.neurchi.advisor.identityaccess.domain.model.access;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.identityaccess.domain.model.identity.TenantId;

import java.time.Instant;

public final class GroupAssignedToRole implements DomainEvent {

    private final TenantId tenantId;
    private final String roleName;
    private final String groupName;
    private final Instant occurredOn;
    private final int eventVersion;

    GroupAssignedToRole(final TenantId tenantId, final String roleName, final String groupName) {
        this.tenantId = tenantId;
        this.roleName = roleName;
        this.groupName = groupName;
        this.occurredOn = Instant.now();
        this.eventVersion = 1;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String roleName() {
        return roleName;
    }

    public String groupName() {
        return groupName;
    }

    @Override
    public int eventVersion() {
        return eventVersion;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
