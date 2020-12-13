package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.time.Instant;

public final class UserAccessTokenExtended implements DomainEvent {

    private final AccessToken accessToken;
    private final TenantId tenantId;
    private final String username;
    private final Instant occurredOn;
    private final int eventVersion;

    UserAccessTokenExtended(
            final TenantId tenantId,
            final String username,
            final AccessToken accessToken) {

        this.accessToken = accessToken;
        this.tenantId = tenantId;
        this.username = username;
        this.occurredOn = Instant.now();
        this.eventVersion = 1;
    }

    public AccessToken accessToken() {
        return this.accessToken;
    }

    public TenantId tenantId() {
        return this.tenantId;
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
