package com.neurchi.advisor.subscription.domain.model.group;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.subscription.domain.model.collaborator.Participant;
import com.neurchi.advisor.subscription.domain.model.tenant.Tenant;

import java.time.Instant;

public final class SubscriptionStarted implements DomainEvent {

    private Participant creator;
    private String description;
    private int eventVersion;
    private GroupId groupId;
    private String exclusiveOwner;
    private CoverPhoto cover;
    private Instant occurredOn;
    private String name;
    private Tenant tenant;

    SubscriptionStarted(
            final Participant creator,
            final String description,
            final GroupId groupId,
            final Instant createdOn,
            final CoverPhoto cover,
            final String name,
            final Tenant tenant) {

        this.creator = creator;
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

    public Participant creator() {
        return creator;
    }

    public String description() {
        return description;
    }

    public GroupId groupId() {
        return groupId;
    }

    public CoverPhoto cover() {
        return cover;
    }

    public String name() {
        return name;
    }

    public Tenant tenant() {
        return tenant;
    }
}
