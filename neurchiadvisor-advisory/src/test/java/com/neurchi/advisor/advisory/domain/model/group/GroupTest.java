package com.neurchi.advisor.advisory.domain.model.group;

import com.neurchi.advisor.advisory.domain.model.subscription.SubscriptionAvailability;
import com.neurchi.advisor.advisory.domain.model.subscription.SubscriptionDescriptor;
import com.neurchi.advisor.advisory.domain.model.team.GroupOwnerId;
import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;
import com.neurchi.advisor.common.domain.model.process.ProcessId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GroupTest extends GroupCommonTest {

    @Test
    public void TestSave() {
        TenantId tenantId = new TenantId("T-12345");

        Group group =
                new Group(
                        tenantId,
                        new GroupId("298448690577160"),
                        new GroupOwnerId(tenantId, "10157329112631392"),
                        "Neurchi de Finances: The Bottom Fishing Club",
                        "Where we fish in deep water",
                        LocalDateTime.of(2017, 4, 19, 10, 59),
                        971,
                        "cover-picture.jpg",
                        SubscriptionAvailability.NotRequested);

        groupRepository.add(group);

        assertNotNull(group);
        assertEquals("Neurchi de Finances: The Bottom Fishing Club", group.name());
        assertEquals("Where we fish in deep water", group.description());
        assertEquals(LocalDateTime.of(2017, 4, 19, 10, 59), group.createdOn());
        assertEquals(971, group.memberCount());
        assertEquals("cover-picture.jpg", group.cover());
        assertEquals(SubscriptionAvailability.NotRequested, group.subscription().availability());
        assertNull(group.subscriptionInitiationId());

        expectedEvents(1);
        expectedEvent(GroupCreated.class);
    }

    @Test
    public void TestRequestAndInitiateSubscription() {
        Group group = this.groupForTest();

        group.requestSubscription(SubscriptionAvailability.Requested);

        assertTrue(group.subscription().descriptor().isUndefined());
        assertEquals(SubscriptionAvailability.Requested, group.subscription().availability());

        expectedEvents(2);
        expectedEvent(GroupCreated.class);
        expectedEvent(GroupSubscriptionRequested.class);

        // eventually...
        ProcessId processId = ProcessId.newProcessId();
        group.startSubscriptionInitiation(processId.id());

        // eventually...
        group.initiateSubscription(new SubscriptionDescriptor("FinanceSubscription12345"));

        expectedEvents(3);
        expectedEvent(GroupSubscriptionInitiated.class);

        assertEquals(processId.id(), group.subscriptionInitiationId());
        assertFalse(group.subscription().descriptor().isUndefined());
        assertEquals(SubscriptionAvailability.Ready, group.subscription().availability());
    }

    @Test
    public void TestRequestAndFailedSubscription() {
        Group group = this.groupForTest();

        group.requestSubscription(SubscriptionAvailability.Requested);

        assertTrue(group.subscription().descriptor().isUndefined());
        assertEquals(SubscriptionAvailability.Requested, group.subscription().availability());

        expectedEvents(2);
        expectedEvent(GroupCreated.class);
        expectedEvent(GroupSubscriptionRequested.class);

        // eventually...
        ProcessId processId = ProcessId.newProcessId();
        group.startSubscriptionInitiation(processId.id());

        // eventually...
        group.failSubscriptionInitiation();

        expectedEvents(2);

        assertNull(group.subscriptionInitiationId());
        assertTrue(group.subscription().descriptor().isUndefined());
        assertEquals(SubscriptionAvailability.Failed, group.subscription().availability());
    }
}
