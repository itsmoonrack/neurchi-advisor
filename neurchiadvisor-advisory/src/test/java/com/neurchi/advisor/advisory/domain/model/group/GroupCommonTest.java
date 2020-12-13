package com.neurchi.advisor.advisory.domain.model.group;

import com.neurchi.advisor.advisory.domain.model.DomainTest;
import com.neurchi.advisor.advisory.domain.model.subscription.SubscriptionAvailability;
import com.neurchi.advisor.advisory.domain.model.team.GroupOwnerId;
import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;

import java.time.LocalDateTime;

public abstract class GroupCommonTest extends DomainTest {

    protected Group groupForTest() {
        TenantId tenantId = new TenantId("T-12345");

        return new Group(
                tenantId,
                new GroupId("298448690577160"),
                new GroupOwnerId(tenantId, "10157329112631392"),
                "Neurchi de Finances: The Bottom Fishing Club",
                "Where we fish in deep water",
                LocalDateTime.of(2017, 4, 19, 10, 59),
                971,
                "cover-picture.jpg",
                SubscriptionAvailability.NotRequested);
    }
}
