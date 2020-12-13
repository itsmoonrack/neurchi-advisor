package com.neurchi.advisor.advisory.domain.model.team;

import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;

import java.util.stream.Stream;

public interface TeamRepository {

    Stream<Team> allTeamsOfTenant(TenantId tenantId);

    void remove(Team team);

    void add(Team team);

    Team teamNamed(TenantId tenantId, String name);
}
