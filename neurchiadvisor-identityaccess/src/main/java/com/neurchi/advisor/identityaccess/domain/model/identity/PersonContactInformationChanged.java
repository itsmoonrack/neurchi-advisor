package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.time.Instant;

public final class PersonContactInformationChanged implements DomainEvent {

    private final ContactInformation contactInformation;
    private final TenantId tenantId;
    private final String username;
    private final Instant occurredOn;
    private final int eventVersion;

    PersonContactInformationChanged(
            final ContactInformation contactInformation,
            final TenantId tenantId,
            final String username) {

        this.contactInformation = contactInformation;
        this.tenantId = tenantId;
        this.username = username;
        this.occurredOn = Instant.now();
        this.eventVersion = 1;
    }

    public ContactInformation contactInformation() {
        return contactInformation;
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
