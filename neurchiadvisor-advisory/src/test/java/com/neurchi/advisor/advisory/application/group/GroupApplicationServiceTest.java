package com.neurchi.advisor.advisory.application.group;

import com.neurchi.advisor.advisory.application.GroupApplicationCommonTest;
import com.neurchi.advisor.advisory.domain.model.group.Group;
import com.neurchi.advisor.advisory.domain.model.group.GroupId;
import com.neurchi.advisor.advisory.domain.model.subscription.SubscriptionAvailability;
import com.neurchi.advisor.advisory.domain.model.team.GroupOwner;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GroupApplicationServiceTest extends GroupApplicationCommonTest {

    @Test
    public void TestSubscriptionProcess() {
        final Group group = this.persistedGroupForTest();

        this.groupApplicationService.requestGroupSubscription(
                new RequestGroupSubscriptionCommand(
                        group.tenantId().id(),
                        group.groupId().id()));

        this.groupApplicationService.startSubscriptionInitiation(
                new StartSubscriptionInitiationCommand(
                        group.tenantId().id(),
                        group.groupId().id()));

        final Group groupWithStartedSubscriptionInitiation =
                this.groupRepository
                        .groupOfId(
                                group.tenantId(),
                                group.groupId())
                        .orElseThrow(IllegalStateException::new);

        assertNotNull(groupWithStartedSubscriptionInitiation.subscriptionInitiationId());

        final String subscriptionId = UUID.randomUUID().toString().toUpperCase();

        final InitiateSubscriptionCommand command =
                new InitiateSubscriptionCommand(
                        group.tenantId().id(),
                        group.groupId().id(),
                        subscriptionId);

        this.groupApplicationService.initiateSubscription(command);

        final Group groupWithInitiatedSubscription =
                this.groupRepository
                        .groupOfId(
                                group.tenantId(),
                                group.groupId())
                        .orElseThrow(IllegalStateException::new);

        assertEquals(subscriptionId, groupWithInitiatedSubscription.subscription().descriptor().id());
    }

    @Test
    public void TestNewGroup() {
        final GroupOwner groupOwner = this.persistedGroupOwnerForTest();

        final String newGroupId =
                this.groupApplicationService.newGroup(
                        new NewGroupCommand(
                                "T-12345",
                                "298448690577160",
                                groupOwner.groupOwnerId().id(),
                                "Neurchi de Finances: The Bottom Fishing Club",
                                "Where we fish in deep water to find the best stock.",
                                971,
                                LocalDateTime.of(2017, 4, 19, 10, 59),
                                "cover-picture.jpg"));

        final Group newGroup =
                this.groupRepository
                        .groupOfId(
                                groupOwner.tenantId(),
                                new GroupId(newGroupId))
                        .orElse(null);

        assertNotNull(newGroup);
        assertEquals("Neurchi de Finances: The Bottom Fishing Club", newGroup.name());
        assertEquals("Where we fish in deep water to find the best stock.", newGroup.description());
        assertEquals(971, newGroup.memberCount());
        assertEquals(LocalDateTime.of(2017, 4, 19, 10, 59), newGroup.createdOn());
        assertEquals("cover-picture.jpg", newGroup.cover());
    }

    @Test
    public void TestNewGroupWithSubscription() {
        final GroupOwner groupOwner = this.persistedGroupOwnerForTest();

        final String newGroupId =
                this.groupApplicationService.newGroupWithSubscription(
                        new NewGroupCommand(
                                "T-12345",
                                "298448690577160",
                                groupOwner.groupOwnerId().id(),
                                "Neurchi de Finances: The Bottom Fishing Club",
                                "Where we fish in deep water to find the best stock.",
                                971,
                                LocalDateTime.of(2017, 4, 19, 10, 59),
                                "cover-picture.jpg"));

        final Group newGroup =
                this.groupRepository
                        .groupOfId(
                                groupOwner.tenantId(),
                                new GroupId(newGroupId))
                        .orElse(null);

        assertNotNull(newGroup);
        assertEquals("Neurchi de Finances: The Bottom Fishing Club", newGroup.name());
        assertEquals("Where we fish in deep water to find the best stock.", newGroup.description());
        assertEquals(971, newGroup.memberCount());
        assertEquals(LocalDateTime.of(2017, 4, 19, 10, 59), newGroup.createdOn());
        assertEquals("cover-picture.jpg", newGroup.cover());
        assertEquals(SubscriptionAvailability.Requested, newGroup.subscription().availability());
    }

    @Test
    public void TestRequestGroupSubscription() {
        final Group group = this.persistedGroupForTest();

        this.groupApplicationService.requestGroupSubscription(
                new RequestGroupSubscriptionCommand(
                        group.tenantId().id(),
                        group.groupId().id()));

        final Group groupWithRequestedSubscription =
                this.groupRepository
                        .groupOfId(
                                group.tenantId(),
                                group.groupId())
                        .orElseThrow(IllegalStateException::new);

        assertEquals(SubscriptionAvailability.Requested, groupWithRequestedSubscription.subscription().availability());
    }

    @Test
    public void TestRetryGroupSubscriptionRequest() {
        final Group group = this.persistedGroupForTest();

        this.groupApplicationService.requestGroupSubscription(
                new RequestGroupSubscriptionCommand(
                        group.tenantId().id(),
                        group.groupId().id()));

        final Group groupWithRequestedSubscription =
                this.groupRepository
                        .groupOfId(
                                group.tenantId(),
                                group.groupId())
                        .orElseThrow(IllegalStateException::new);

        assertEquals(SubscriptionAvailability.Requested, groupWithRequestedSubscription.subscription().availability());

        this.groupApplicationService.startSubscriptionInitiation(
                new StartSubscriptionInitiationCommand(
                        group.tenantId().id(),
                        group.groupId().id()));

        final Group groupWithSubscriptionInitiation =
                this.groupRepository
                        .groupOfId(
                                group.tenantId(),
                                group.groupId())
                        .orElseThrow(IllegalStateException::new);

        assertNotNull(groupWithSubscriptionInitiation.subscriptionInitiationId());

        this.groupApplicationService.retryGroupSubscriptionRequest(
                new RetryGroupSubscriptionRequestCommand(
                        group.tenantId().id(),
                        groupWithSubscriptionInitiation.subscriptionInitiationId()));

        final Group groupWithRetriedRequestedSubscription =
                this.groupRepository
                        .groupOfId(
                                group.tenantId(),
                                group.groupId())
                        .orElseThrow(IllegalStateException::new);

        assertEquals(SubscriptionAvailability.Requested, groupWithRetriedRequestedSubscription.subscription().availability());
    }

    @Test
    public void TestStartSubscriptionInitiation() {
        final Group group = this.persistedGroupForTest();

        this.groupApplicationService.requestGroupSubscription(
                new RequestGroupSubscriptionCommand(
                        group.tenantId().id(),
                        group.groupId().id()));

        final Group groupWithRequestedSubscription =
                this.groupRepository
                        .groupOfId(
                                group.tenantId(),
                                group.groupId())
                        .orElseThrow(IllegalStateException::new);

        assertEquals(SubscriptionAvailability.Requested, groupWithRequestedSubscription.subscription().availability());

        assertNull(groupWithRequestedSubscription.subscriptionInitiationId());

        this.groupApplicationService.startSubscriptionInitiation(
                new StartSubscriptionInitiationCommand(
                        group.tenantId().id(),
                        group.groupId().id()));

        final Group groupWithSubscriptionInitiation =
                this.groupRepository
                        .groupOfId(
                                group.tenantId(),
                                group.groupId())
                        .orElseThrow(IllegalStateException::new);

        assertNotNull(groupWithSubscriptionInitiation.subscriptionInitiationId());
    }

    @Test
    public void TestTimeOutGroupSubscriptionRequest() {
        final Group group = this.persistedGroupForTest();

        this.groupApplicationService.requestGroupSubscription(
                new RequestGroupSubscriptionCommand(
                        group.tenantId().id(),
                        group.groupId().id()));

        final Group groupWithRequestedSubscription =
                this.groupRepository
                        .groupOfId(
                                group.tenantId(),
                                group.groupId())
                        .orElseThrow(IllegalStateException::new);

        assertEquals(SubscriptionAvailability.Requested, groupWithRequestedSubscription.subscription().availability());

        this.groupApplicationService.startSubscriptionInitiation(
                new StartSubscriptionInitiationCommand(
                        group.tenantId().id(),
                        group.groupId().id()));

        final Group groupWithSubscriptionInitiation =
                this.groupRepository
                        .groupOfId(
                                group.tenantId(),
                                group.groupId())
                        .orElseThrow(IllegalStateException::new);

        assertNotNull(groupWithSubscriptionInitiation.subscriptionInitiationId());

        this.groupApplicationService.timeOutGroupSubscriptionRequest(
                new TimeOutGroupSubscriptionRequestCommand(
                        group.tenantId().id(),
                        groupWithSubscriptionInitiation.subscriptionInitiationId(),
                        Instant.now()));

        final Group groupWithTimedOutRequestedSubscription =
                this.groupRepository
                        .groupOfId(
                                group.tenantId(),
                                group.groupId())
                        .orElseThrow(IllegalStateException::new);

        assertEquals(SubscriptionAvailability.Failed, groupWithTimedOutRequestedSubscription.subscription().availability());
    }
}