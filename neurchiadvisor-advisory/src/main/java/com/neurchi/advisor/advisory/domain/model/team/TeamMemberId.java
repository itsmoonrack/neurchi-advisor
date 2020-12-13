package com.neurchi.advisor.advisory.domain.model.team;

import com.neurchi.advisor.advisory.domain.model.ValueObject;
import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;

import java.util.Objects;

public class TeamMemberId extends ValueObject {

    private String id;
    private TenantId tenantId;

    TeamMemberId(final TenantId tenantId, final String id) {
        this.setId(id);
        this.setTenantId(tenantId);
    }

    public String id() {
        return this.id;
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TeamMemberId that = (TeamMemberId) o;
        return id.equals(that.id) &&
                tenantId.equals(that.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenantId);
    }

    protected TeamMemberId() {
        // Needed by Hibernate.
    }

    private void setId(final String id) {
        this.assertArgumentNotEmpty(id, "The id must be provided.");
        this.assertArgumentLength(id, 36, "The id must be 36 characters or less.");

        this.id = id;
    }

    private void setTenantId(final TenantId tenantId) {
        this.assertArgumentNotNull(tenantId, "The tenantId must be provided.");

        this.tenantId = tenantId;
    }
}
