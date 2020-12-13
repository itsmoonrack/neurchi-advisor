package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.IdentifiedDomainObject;

import java.util.Objects;

public final class GroupMember extends IdentifiedDomainObject {

    private String name;
    private TenantId tenantId;
    private GroupMemberType type;

    protected GroupMember(final TenantId tenantId, final String name, final GroupMemberType type) {
        setName(name);
        setTenantId(tenantId);
        setType(type);
    }

    public boolean isGroup() {
        return type.isGroup();
    }

    public boolean isUser() {
        return type.isUser();
    }

    public String name() {
        return name;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    protected void setName(final String name) {
        this.assertArgumentNotNull(name, "Member name is required.");
        this.assertArgumentLength(name, 100, "Member name must be 100 characters or less.");

        this.name = name;
    }

    protected void setTenantId(final TenantId tenantId) {
        this.assertArgumentNotNull(tenantId, "TenantId is required.");

        this.tenantId = tenantId;
    }

    protected void setType(final GroupMemberType type) {
        this.assertArgumentNotNull(tenantId, "Member Type is required.");

        this.type = type;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final GroupMember that = (GroupMember) o;
        return name.equals(that.name) &&
                tenantId.equals(that.tenantId) &&
                type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tenantId, type);
    }

    protected GroupMember() {
        // Needed by Hibernate.
    }
}
