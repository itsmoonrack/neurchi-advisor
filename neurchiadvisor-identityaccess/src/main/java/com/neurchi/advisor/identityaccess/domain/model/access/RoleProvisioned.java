package com.neurchi.advisor.identityaccess.domain.model.access;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.identityaccess.domain.model.identity.TenantId;

import java.time.Instant;

public final class RoleProvisioned implements DomainEvent {

    private final String name;
    private final TenantId tenantId;
    private final Instant occurredOn;
    private final int eventVersion;

    public RoleProvisioned(final TenantId tenantId, final String name) {
        this.name = name;
        this.tenantId = tenantId;
        this.occurredOn = Instant.now();
        this.eventVersion = 1;
    }

    public String name() {
        return name;
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
