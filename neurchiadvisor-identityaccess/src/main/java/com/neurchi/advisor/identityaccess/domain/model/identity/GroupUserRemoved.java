package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.time.Instant;

public final class GroupUserRemoved implements DomainEvent {

    private final TenantId tenantId;
    private final String groupName;
    private final String username;
    private final Instant occurredOn;
    private final int eventVersion;

    GroupUserRemoved(final TenantId tenantId, final String groupName, final String username) {
        this.tenantId = tenantId;
        this.groupName = groupName;
        this.username = username;
        this.occurredOn = Instant.now();
        this.eventVersion = 1;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String groupName() {
        return groupName;
    }

    public String username() {
        return username;
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
