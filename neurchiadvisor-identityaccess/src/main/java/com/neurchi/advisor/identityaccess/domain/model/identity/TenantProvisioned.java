package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.time.Instant;

public final class TenantProvisioned implements DomainEvent {

    private final TenantId tenantId;
    private final Instant occurredOn;
    private final int eventVersion;

    TenantProvisioned(final TenantId tenantId) {
        this.tenantId = tenantId;
        this.occurredOn = Instant.now();
        this.eventVersion = 1;
    }

    public TenantId tenantId() {
        return tenantId;
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
