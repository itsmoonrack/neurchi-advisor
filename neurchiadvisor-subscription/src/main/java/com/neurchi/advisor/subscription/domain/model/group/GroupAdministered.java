package com.neurchi.advisor.subscription.domain.model.group;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.subscription.domain.model.collaborator.Administrator;
import com.neurchi.advisor.subscription.domain.model.tenant.Tenant;

import java.time.Instant;

public final class GroupAdministered implements DomainEvent {

    private Tenant tenant;
    private GroupId groupId;
    private Administrator administrator;
    private int eventVersion;
    private Instant occurredOn;

    GroupAdministered(
            final Tenant tenant,
            final GroupId groupId,
            final Administrator administrator) {

        this.eventVersion = 1;
        this.groupId = groupId;
        this.occurredOn = Instant.now();
        this.administrator = administrator;
        this.tenant = tenant;
    }

    @Override
    public int eventVersion() {
        return this.eventVersion;
    }

    @Override
    public Instant occurredOn() {
        return this.occurredOn;
    }

    public GroupId groupId() {
        return this.groupId;
    }

    public Administrator administrator() {
        return this.administrator;
    }

    public Tenant tenant() {
        return this.tenant;
    }
}
