package com.neurchi.advisor.advisory.domain.model.group;

import com.neurchi.advisor.advisory.domain.model.team.GroupOwnerId;
import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;
import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.time.Instant;

public class GroupCreated implements DomainEvent {

    private TenantId tenantId;
    private GroupId groupId;
    private GroupOwnerId groupOwnerId;
    private String name;
    private String description;
    private Instant createdOn;
    private String cover;
    private Integer memberCount;
    private boolean requestingSubscription;
    private int eventVersion;

    GroupCreated(
            final TenantId tenantId,
            final GroupId groupId,
            final GroupOwnerId groupOwnerId,
            final String name,
            final String description,
            final Instant createdOn,
            final String cover,
            final Integer memberCount,
            final boolean requestingSubscription) {

        this.tenantId = tenantId;
        this.groupId = groupId;
        this.groupOwnerId = groupOwnerId;
        this.name = name;
        this.description = description;
        this.createdOn = createdOn;
        this.cover = cover;
        this.memberCount = memberCount;
        this.requestingSubscription = requestingSubscription;
        this.eventVersion = 1;
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    public GroupId groupId() {
        return this.groupId;
    }

    public GroupOwnerId groupOwnerId() {
        return this.groupOwnerId;
    }

    public String name() {
        return this.name;
    }

    public String description() {
        return this.description;
    }

    public Instant createdOn() {
        return this.createdOn;
    }

    public String cover() {
        return this.cover;
    }

    public Integer memberCount() {
        return this.memberCount;
    }

    public boolean requestingSubscription() {
        return this.requestingSubscription;
    }

    @Override
    public int eventVersion() {
        return this.eventVersion;
    }

    @Override
    public Instant occurredOn() {
        return this.createdOn;
    }
}
