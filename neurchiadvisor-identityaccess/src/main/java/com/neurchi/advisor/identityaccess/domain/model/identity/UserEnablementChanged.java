package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.time.Instant;

public final class UserEnablementChanged implements DomainEvent {

    private final Enablement enablement;
    private final TenantId tenantId;
    private final String username;
    private final Instant occurredOn;
    private final int eventVersion;

    public UserEnablementChanged(
            TenantId tenantId,
            String username,
            Enablement enablement) {

        this.enablement = enablement;
        this.tenantId = tenantId;
        this.username = username;
        this.occurredOn = Instant.now();
        this.eventVersion = 1;
    }

    public Enablement enablement() {
        return enablement;
    }

    public TenantId tenantId() {
        return tenantId;
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
