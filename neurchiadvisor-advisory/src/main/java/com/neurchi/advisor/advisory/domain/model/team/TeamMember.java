package com.neurchi.advisor.advisory.domain.model.team;

import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;

import java.time.Instant;

public class TeamMember extends Member {

    public TeamMember(
            final TenantId tenantId,
            final String username,
            final String name,
            final String firstName,
            final String lastName,
            final String picture,
            final String emailAddress,
            final Instant initializedOn) {

        super(tenantId, username, name, firstName, lastName, picture, emailAddress, initializedOn);
    }

    public TeamMemberId teamMemberId() {
        return new TeamMemberId(this.tenantId(), this.username());
    }

    protected TeamMember() {
        // Needed by Hibernate
    }
}
