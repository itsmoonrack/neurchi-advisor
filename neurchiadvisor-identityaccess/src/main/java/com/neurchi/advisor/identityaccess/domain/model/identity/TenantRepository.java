package com.neurchi.advisor.identityaccess.domain.model.identity;

import java.util.Optional;

public interface TenantRepository {

    void add(Tenant tenant);

    TenantId nextIdentity();

    void remove(Tenant tenant);

    Optional<Tenant> tenantNamed(String name);

    Optional<Tenant> tenantOfId(TenantId tenantId);
}
