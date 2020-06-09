package com.neurchi.advisor.subscription.domain.model.group;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.subscription.domain.model.collaborator.Collaborator;
import com.neurchi.advisor.subscription.domain.model.tenant.Tenant;

import java.time.Instant;

public final class GroupMemberCountChanged implements DomainEvent {

    private Tenant tenant;
    private GroupId groupId;
    private Collaborator reporter;
    private int memberCount;
    private boolean genuine;
    private int eventVersion;
    private Instant occurredOn;

    GroupMemberCountChanged(
            final Tenant tenant,
            final GroupId groupId,
            final Collaborator reporter,
            final int memberCount,
            final boolean genuine) {

        this.tenant = tenant;
        this.groupId = groupId;
        this.reporter = reporter;
        this.memberCount = memberCount;
        this.genuine = genuine;
        this.eventVersion = 1;
        this.occurredOn = Instant.now();
    }

    public Tenant tenant() {
        return this.tenant;
    }

    public GroupId groupId() {
        return this.groupId;
    }

    public Collaborator reporter() {
        return this.reporter;
    }

    public int memberCount() {
        return this.memberCount;
    }

    public boolean genuine() {
        return this.genuine;
    }

    @Override
    public int eventVersion() {
        return this.eventVersion;
    }

    @Override
    public Instant occurredOn() {
        return this.occurredOn;
    }
}
