package com.neurchi.advisor.subscription.domain.model.group;

import com.neurchi.advisor.subscription.domain.model.tenant.Tenant;

public interface GroupRepository {

    Group groupOfId(Tenant tenant, GroupId groupId);

    void save(Group group);

}
