package com.neurchi.advisor.subscription.domain.model.group;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.common.domain.model.EventSourcedRootEntity;
import com.neurchi.advisor.subscription.domain.model.collaborator.Subscriber;
import com.neurchi.advisor.subscription.domain.model.tenant.Tenant;

import java.util.Objects;
import java.util.stream.Stream;

public final class Subscription extends EventSourcedRootEntity {

    private Tenant tenant;
    private GroupId groupId;
    private SubscriptionId subscriptionId;
    private Subscriber subscriber;
    private boolean closed;

    public Subscription(final Stream<DomainEvent> eventStream, final int streamVersion) {
        super(eventStream, streamVersion);
    }

    public Subscriber subscriber() {
        return this.subscriber;
    }

    public void close() {
        this.assertStateFalse(this.isClosed(), "This subscription is already closed.");

        this.apply(new SubscriptionClosed(this.tenant(), this.groupId(), this.subscriptionId(), this.subscriber()));
    }

    public boolean isClosed() {
        return this.closed;
    }

    public SubscriptionId subscriptionId() {
        return this.subscriptionId;
    }

    public GroupId groupId() {
        return this.groupId;
    }

    public void reopen() {
        this.assertStateTrue(this.isClosed(), "This subscription is not closed.");

        this.apply(new SubscriptionReopened(this.tenant(), this.groupId(), this.subscriptionId(), this.subscriber()));
    }

    public Tenant tenant() {
        return this.tenant;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Subscription that = (Subscription) o;
        return tenant.equals(that.tenant) &&
                groupId.equals(that.groupId) &&
                subscriptionId.equals(that.subscriptionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenant, groupId, subscriptionId);
    }

    protected Subscription(
            final Tenant tenant,
            final GroupId groupId,
            final SubscriptionId subscriptionId,
            final Subscriber subscriber) {

        this.assertArgumentNotNull(tenant, "The tenant must be provided.");
        this.assertArgumentNotNull(groupId, "The group id must be provided.");
        this.assertArgumentNotNull(subscriptionId, "The subscription id must be provided.");
        this.assertArgumentNotNull(subscriber, "The subscriber must be provided.");

        this.apply(new SubscriptionStarted(tenant, groupId, subscriptionId, subscriber));
    }

    protected void when(final SubscriptionClosed event) {
        this.closed = true;
    }

    protected void when(final SubscriptionReopened event) {
        this.closed = false;
    }

    protected void when(final SubscriptionStarted event) {
        this.subscriber = event.subscriber();
        this.subscriptionId = event.subscriptionId();
        this.groupId = event.groupId();
        this.tenant = event.tenant();
    }
}
