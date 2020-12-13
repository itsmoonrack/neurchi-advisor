package com.neurchi.advisor.advisory.domain.model.group;

import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;

import java.util.Optional;
import java.util.stream.Stream;

public interface GroupRepository {

    Stream<Group> allGroupsOfTenant(TenantId tenantId);

    Optional<Group> groupOfSubscriptionInitiationId(TenantId tenantId, String subscriptionInitiationId);

    Optional<Group> groupOfId(TenantId tenantId, GroupId groupId);

    void remove(Group group);

    void add(Group group);
}
