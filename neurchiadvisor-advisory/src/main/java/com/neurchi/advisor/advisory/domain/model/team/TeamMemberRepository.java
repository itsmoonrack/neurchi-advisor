package com.neurchi.advisor.advisory.domain.model.team;

import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;

import java.util.Optional;
import java.util.stream.Stream;

public interface TeamMemberRepository {

    Stream<TeamMember> allTeamMembersOfTenant(TenantId tenantId);

    void remove(TeamMember teamMember);

    void add(TeamMember teamMember);

    Optional<TeamMember> teamMemberOfIdentity(TenantId tenantId, String username);
}
