package com.neurchi.advisor.subscription.domain.model.group;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.subscription.domain.model.tenant.Tenant;

import java.time.Instant;

public final class GroupCoverChanged implements DomainEvent {

    private Tenant tenant;
    private GroupId groupId;
    private CoverPhoto cover;
    private int eventVersion;
    private Instant occurredOn;

    GroupCoverChanged(
            final Tenant tenant,
            final GroupId groupId,
            final CoverPhoto cover) {

        this.eventVersion = 1;
        this.groupId = groupId;
        this.occurredOn = Instant.now();
        this.cover = cover;
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

    public CoverPhoto cover() {
        return this.cover;
    }

    public Tenant tenant() {
        return this.tenant;
    }
}
