package com.neurchi.advisor.advisory.domain.model.group;

import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;
import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.time.Instant;

public class GroupSubscriptionInitiated implements DomainEvent {

    private TenantId tenantId;
    private GroupId groupId;
    private Instant occurredOn;
    private int eventVersion;
    private GroupSubscription groupSubscription;

    GroupSubscriptionInitiated(
            final TenantId tenantId,
            final GroupId groupId,
            final GroupSubscription groupSubscription) {

        this.tenantId = tenantId;
        this.groupId = groupId;
        this.occurredOn = Instant.now();
        this.groupSubscription = groupSubscription;
        this.eventVersion = 1;
    }

    @Override
    public int eventVersion() {
        return this.eventVersion;
    }

    @Override
    public Instant occurredOn() {
        return this.occurredOn;
    }

    public GroupSubscription groupSubscription() {
        return this.groupSubscription;
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    public GroupId groupId() {
        return this.groupId;
    }
}
