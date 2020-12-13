package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.time.Instant;

public final class GroupGroupAdded implements DomainEvent {

    private final TenantId tenantId;
    private final String groupName;
    private final String nestedGroupName;
    private final Instant occurredOn;
    private final int eventVersion;

    GroupGroupAdded(final TenantId tenantId, final String groupName, final String nestedGroupName) {
        this.tenantId = tenantId;
        this.groupName = groupName;
        this.nestedGroupName = nestedGroupName;
        this.occurredOn = Instant.now();
        this.eventVersion = 1;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String groupName() {
        return groupName;
    }

    public String nestedGroupName() {
        return nestedGroupName;
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
