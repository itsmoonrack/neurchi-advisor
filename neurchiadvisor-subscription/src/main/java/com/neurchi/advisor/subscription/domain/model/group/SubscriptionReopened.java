package com.neurchi.advisor.subscription.domain.model.group;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.subscription.domain.model.collaborator.Subscriber;
import com.neurchi.advisor.subscription.domain.model.tenant.Tenant;

import java.time.Instant;

public final class SubscriptionReopened implements DomainEvent {

    private SubscriptionId subscriptionId;
    private int eventVersion;
    private Subscriber subscriber;
    private GroupId groupId;
    private Instant occurredOn;
    private Tenant tenant;

    SubscriptionReopened(
            final Tenant tenant,
            final GroupId groupId,
            final SubscriptionId subscriptionId,
            final Subscriber subscriber) {

        this.subscriptionId = subscriptionId;
        this.subscriber = subscriber;
        this.eventVersion = 1;
        this.groupId = groupId;
        this.occurredOn = Instant.now();
        this.tenant = tenant;
    }

    public SubscriptionId subscriptionId() {
        return this.subscriptionId;
    }

    @Override
    public int eventVersion() {
        return this.eventVersion;
    }

    @Override
    public Instant occurredOn() {
        return this.occurredOn;
    }

    public Subscriber subscriber() {
        return this.subscriber;
    }

    public GroupId groupId() {
        return this.groupId;
    }

    public Tenant tenant() {
        return this.tenant;
    }
}
