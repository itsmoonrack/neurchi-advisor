package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.time.Instant;

public final class UserRegistered implements DomainEvent {

    private final TenantId tenantId;
    private final String username;
    private final FullName fullName;
    private final EmailAddress emailAddress;
    private final Instant occurredOn;
    private final int eventVersion;

    UserRegistered(
            final TenantId tenantId,
            final String username,
            final FullName fullName,
            final EmailAddress emailAddress) {

        this.tenantId = tenantId;
        this.username = username;
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.occurredOn = Instant.now();
        this.eventVersion = 1;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String username() {
        return username;
    }

    public FullName fullName() {
        return fullName;
    }

    public EmailAddress emailAddress() {
        return emailAddress;
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
