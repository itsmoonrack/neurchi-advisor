package com.neurchi.advisor.subscription.domain.model.group;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.subscription.domain.model.tenant.Tenant;

import java.time.Instant;

public final class GroupProvisionned implements DomainEvent {

    private String description;
    private int eventVersion;
    private GroupId groupId;
    private String cover;
    private Instant occurredOn;
    private String name;
    private Tenant tenant;

    GroupProvisionned(
            final Tenant tenant,
            final GroupId groupId,
            final String name,
            final String description,
            final Instant createdOn,
            final String cover) {

        this.description = description;
        this.eventVersion = 1;
        this.groupId = groupId;
        this.cover = cover;
        this.occurredOn = createdOn;
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

    public String description() {
        return description;
    }

    public GroupId groupId() {
        return groupId;
    }

    public String cover() {
        return cover;
    }

    public String name() {
        return name;
    }

    public Tenant tenant() {
        return tenant;
    }
}
