package com.neurchi.advisor.subscription.domain.model.group;

import com.neurchi.advisor.subscription.domain.model.tenant.Tenant;

import java.util.Optional;

public interface GroupRepository {

    Optional<Group> groupOfId(Tenant tenant, GroupId groupId);

    void save(Group group);

}
