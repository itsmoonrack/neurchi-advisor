package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.time.Instant;

public final class PersonNameChanged implements DomainEvent {

    private final FullName name;
    private final TenantId tenantId;
    private final String username;
    private final Instant occurredOn;
    private final int eventVersion;

    PersonNameChanged(final TenantId tenantId, final String username, final FullName name) {
        this.name = name;
        this.tenantId = tenantId;
        this.username = username;
        this.occurredOn = Instant.now();
        this.eventVersion = 1;
    }

    public FullName name() {
        return name;
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
