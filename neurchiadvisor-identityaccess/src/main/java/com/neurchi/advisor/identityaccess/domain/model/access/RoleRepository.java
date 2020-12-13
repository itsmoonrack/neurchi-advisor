package com.neurchi.advisor.identityaccess.domain.model.access;

import com.neurchi.advisor.identityaccess.domain.model.identity.TenantId;

import java.util.Optional;
import java.util.stream.Stream;

public interface RoleRepository {

    void add(Role role);

    Stream<Role> allRoles(TenantId tenantId);

    void remove(Role role);

    Optional<Role> roleNamed(TenantId tenantId, String roleName);
}
