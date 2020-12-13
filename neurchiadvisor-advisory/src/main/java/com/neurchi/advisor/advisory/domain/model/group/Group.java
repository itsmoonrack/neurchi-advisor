package com.neurchi.advisor.advisory.domain.model.group;

import com.neurchi.advisor.advisory.domain.model.subscription.SubscriptionAvailability;
import com.neurchi.advisor.advisory.domain.model.subscription.SubscriptionDescriptor;
import com.neurchi.advisor.advisory.domain.model.team.GroupOwnerId;
import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;
import com.neurchi.advisor.common.domain.model.ConcurrencySafeEntity;
import com.neurchi.advisor.common.domain.model.DomainEventPublisher;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

public class Group extends ConcurrencySafeEntity {

    private String description;
    private GroupSubscription subscription;
    private String subscriptionInitiationId;
    private String name;
    private Integer memberCount;
    private LocalDateTime createdOn; // TODO: LocalDateTime ?
    private String cover;
    private GroupId groupId;
    private GroupOwnerId groupOwnerId;
    private TenantId tenantId;

    public Group(
            final TenantId tenantId,
            final GroupId groupId,
            final GroupOwnerId groupOwnerId,
            final String name,
            final String description,
            final LocalDateTime createdOn,
            final Integer memberCount,
            final String cover,
            final SubscriptionAvailability subscriptionAvailability) {

        this.setTenantId(tenantId); // must precede groupOwnerId for compare
        this.setDescription(description);
        this.setSubscription(GroupSubscription.fromAvailability(subscriptionAvailability));
        this.setSubscriptionInitiationId(null);
        this.setName(name);
        this.setMemberCount(memberCount);
        this.setCreatedOn(createdOn);
        this.setCover(cover);
        this.setGroupId(groupId);
        this.setGroupOwnerId(groupOwnerId);

        DomainEventPublisher
                .instance()
                .publish(new GroupCreated(
                        this.tenantId(),
                        this.groupId(),
                        this.groupOwnerId(),
                        this.name(),
                        this.description(),
                        this.createdOn().toInstant(ZoneOffset.UTC),
                        this.cover(),
                        this.memberCount(),
                        this.subscription().availability().isRequested()));
    }

    public String description() {
        return this.description;
    }

    public GroupSubscription subscription() {
        return this.subscription;
    }

    public String subscriptionInitiationId() {
        return this.subscriptionInitiationId;
    }

    public void failSubscriptionInitiation() {
        if (!this.subscription().availability().isReady()) {
            this.setSubscriptionInitiationId(null);
            this.setSubscription(
                    GroupSubscription
                            .fromAvailability(
                                    SubscriptionAvailability.Failed));
        }
    }

    public void initiateSubscription(final SubscriptionDescriptor descriptor) {
        this.assertArgumentNotNull(descriptor, "Descriptor must be provided.");

        if (this.subscription().availability().isRequested()) {
            this.setSubscription(this.subscription().nowReady(descriptor));

            DomainEventPublisher
                    .instance()
                    .publish(new GroupSubscriptionInitiated(
                            this.tenantId(),
                            this.groupId(),
                            this.subscription()));
        }
    }

    public String name() {
        return this.name;
    }

    public Integer memberCount() {
        return this.memberCount;
    }

    public LocalDateTime createdOn() {
        return this.createdOn;
    }

    public String cover() {
        return this.cover;
    }

    public GroupId groupId() {
        return this.groupId;
    }

    public GroupOwnerId groupOwnerId() {
        return this.groupOwnerId;
    }

    public void startSubscriptionInitiation(final String subscriptionInitiationId) {
        if (!this.subscription().availability().isReady()) {
            this.setSubscriptionInitiationId(subscriptionInitiationId);
        }
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    public void requestSubscription(final SubscriptionAvailability subscriptionAvailability) {
        if (!this.subscription().availability().isReady()) {
            this.setSubscription(
                    GroupSubscription.fromAvailability(
                            subscriptionAvailability));

            DomainEventPublisher
                    .instance()
                    .publish(new GroupSubscriptionRequested(
                            this.tenantId(),
                            this.groupId(),
                            this.groupOwnerId(),
                            this.name(),
                            this.description(),
                            this.createdOn().toInstant(ZoneOffset.UTC),
                            this.cover(),
                            this.memberCount(),
                            this.subscription().availability().isRequested()));
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Group group = (Group) o;
        return tenantId.equals(group.tenantId) &&
                groupId.equals(group.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, groupId);
    }

    @Override
    public String toString() {
        return "Group{" +
                "description='" + description + '\'' +
                ", subscription=" + subscription +
                ", subscriptionInitiationId='" + subscriptionInitiationId + '\'' +
                ", name='" + name + '\'' +
                ", memberCount=" + memberCount +
                ", createdOn=" + createdOn +
                ", cover='" + cover + '\'' +
                ", groupId=" + groupId +
                ", groupOwnerId=" + groupOwnerId +
                ", tenantId=" + tenantId +
                '}';
    }

    protected Group() {
        // Needed by Hibernate.
    }

    private void setDescription(final String description) {
        if (description != null) {
            this.assertArgumentLength(
                    description,
                    65535,
                    "Description must be 65535 characters or less.");
        }

        this.description = description;
    }

    private void setSubscription(final GroupSubscription subscription) {
        this.assertArgumentNotNull(subscription, "The subscription is required even if it is unused.");

        this.subscription = subscription;
    }

    private void setSubscriptionInitiationId(final String subscriptionInitiationId) {
        if (subscriptionInitiationId != null) {
            this.assertArgumentLength(
                    subscriptionInitiationId,
                    100,
                    "Subscription initiation identity must be 100 characters or less.");
        }

        this.subscriptionInitiationId = subscriptionInitiationId;
    }

    private void setName(final String name) {
        this.assertArgumentNotEmpty(name, "The name must be provided.");
        this.assertArgumentLength(name, 75, "The name must be 75 characters or less.");

        this.name = name;
    }

    private void setMemberCount(final Integer memberCount) {
        this.memberCount = memberCount;
    }

    private void setCreatedOn(final LocalDateTime createdOn) {
        this.assertArgumentNotNull(createdOn, "The created date must be provided.");

        this.createdOn = createdOn;
    }

    private void setCover(final String cover) {
        if (cover != null) {
            this.assertArgumentLength(
                    cover,
                    255,
                    "The cover must be 255 characters or less.");
        }

        this.cover = cover;
    }

    private void setGroupId(final GroupId groupId) {
        this.assertArgumentNotNull(groupId, "The groupId must be provided.");

        this.groupId = groupId;
    }

    private void setGroupOwnerId(final GroupOwnerId groupOwnerId) {
        this.assertArgumentNotNull(groupOwnerId, "The groupOwnerId must be provided.");
        this.assertArgumentEquals(this.tenantId(), groupOwnerId.tenantId(), "The groupOwnerId must have the same tenant.");

        this.groupOwnerId = groupOwnerId;
    }

    private void setTenantId(final TenantId tenantId) {
        this.assertArgumentNotNull(tenantId, "The tenantId must be provided.");

        this.tenantId = tenantId;
    }
}
