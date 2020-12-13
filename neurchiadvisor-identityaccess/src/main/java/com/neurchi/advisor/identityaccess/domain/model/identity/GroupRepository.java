package com.neurchi.advisor.identityaccess.domain.model.identity;

import java.util.Optional;
import java.util.stream.Stream;

public interface GroupRepository {

    void add(Group group);

    Stream<Group> allGroups(TenantId tenantId);

    Optional<Group> groupNamed(TenantId tenantId, String groupName);

    void remove(Group group);

}
