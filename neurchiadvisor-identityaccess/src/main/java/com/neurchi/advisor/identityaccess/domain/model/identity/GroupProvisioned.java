package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.time.Instant;

public final class GroupProvisioned implements DomainEvent {

    private TenantId tenantId;
    private String name;
    private final Instant occurredOn;
    private final int eventVersion;

    GroupProvisioned(final TenantId tenantId, final String name) {
        this.tenantId = tenantId;
        this.name = name;
        this.occurredOn = Instant.now();
        this.eventVersion = 1;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String name() {
        return name;
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
