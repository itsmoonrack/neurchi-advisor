package com.neurchi.advisor.identityaccess.domain.model.access;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.identityaccess.domain.model.identity.TenantId;

import java.time.Instant;

public final class UserAssignedToRole implements DomainEvent {

    private final TenantId tenantId;
    private final String roleName;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String emailAddress;
    private final Instant occurredOn;
    private final int eventVersion;

    UserAssignedToRole(
            final TenantId tenantId,
            final String roleName,
            final String username,
            final String firstName,
            final String lastName,
            final String emailAddress) {

        this.tenantId = tenantId;
        this.roleName = roleName;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.occurredOn = Instant.now();
        this.eventVersion = 1;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String roleName() {
        return roleName;
    }

    public String username() {
        return username;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public String emailAddress() {
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
