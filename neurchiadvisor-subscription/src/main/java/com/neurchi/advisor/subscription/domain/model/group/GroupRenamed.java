package com.neurchi.advisor.subscription.domain.model.group;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.subscription.domain.model.tenant.Tenant;

import java.time.Instant;

public final class GroupRenamed implements DomainEvent {

    private Tenant tenant;
    private GroupId groupId;
    private String name;
    private int eventVersion;
    private Instant occurredOn;

    GroupRenamed(
            final Tenant tenant,
            final GroupId groupId,
            final String name) {

        this.eventVersion = 1;
        this.groupId = groupId;
        this.occurredOn = Instant.now();
        this.name = name;
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
        return groupId;
    }

    public String name() {
        return name;
    }

    public Tenant tenant() {
        return tenant;
    }
}
