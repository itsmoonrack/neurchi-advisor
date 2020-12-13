package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.time.Instant;

public final class TenantAdministratorRegistered implements DomainEvent {

    private final TenantId tenantId;
    private final String tenantName;
    private final FullName administratorName;
    private final EmailAddress emailAddress;
    private final String username;
    private final Instant occurredOn;
    private final int eventVersion;

    TenantAdministratorRegistered(
            final TenantId tenantId,
            final String tenantName,
            final FullName administratorName,
            final EmailAddress emailAddress,
            final String username) {

        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.administratorName = administratorName;
        this.emailAddress = emailAddress;
        this.username = username;
        this.occurredOn = Instant.now();
        this.eventVersion = 1;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String tenantName() {
        return this.tenantName;
    }

    public FullName administratorName() {
        return this.administratorName;
    }

    public EmailAddress emailAddress() {
        return this.emailAddress;
    }

    public String username() {
        return this.username;
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
