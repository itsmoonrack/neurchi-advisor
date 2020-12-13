package com.neurchi.advisor.advisory.domain.model.team;

import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;

import java.util.Optional;
import java.util.stream.Stream;

public interface GroupOwnerRepository {

    Stream<GroupOwner> allGroupOwnersOfTenant(TenantId tenantId);

    void remove(GroupOwner groupOwner);

    void add(GroupOwner groupOwner);

    Optional<GroupOwner> groupOwnerOfIdentity(TenantId tenantId, String username);
}
