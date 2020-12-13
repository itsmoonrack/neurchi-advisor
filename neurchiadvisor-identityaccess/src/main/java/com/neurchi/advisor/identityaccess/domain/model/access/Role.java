package com.neurchi.advisor.identityaccess.domain.model.access;

import com.neurchi.advisor.common.domain.model.ConcurrencySafeEntity;
import com.neurchi.advisor.common.domain.model.DomainEventPublisher;
import com.neurchi.advisor.identityaccess.domain.model.identity.Group;
import com.neurchi.advisor.identityaccess.domain.model.identity.GroupMemberService;
import com.neurchi.advisor.identityaccess.domain.model.identity.TenantId;
import com.neurchi.advisor.identityaccess.domain.model.identity.User;

import java.util.Objects;
import java.util.UUID;

public class Role extends ConcurrencySafeEntity {

    private TenantId tenantId;
    private String name;
    private Group group;
    private String description;
    private boolean supportsNesting;

    public Role(final TenantId tenantId, final String name, final String description) {
        this(tenantId, name, description, false);
    }

    public Role(final TenantId tenantId, final String name, final String description, final boolean supportsNesting) {
        this.setDescription(description);
        this.setName(name);
        this.setSupportsNesting(supportsNesting);
        this.setTenantId(tenantId);

        this.createInternalGroup();
    }

    public void assignGroup(final Group group, final GroupMemberService groupMemberService) {
        this.assertStateTrue(this.supportsNesting(), "This role does not support group nesting.");
        this.assertArgumentNotNull(group, "Group must not be null.");
        this.assertArgumentEquals(this.tenantId(), group.tenantId(), "Wrong tenant for this group.");

        this.group().addGroup(group, groupMemberService);

        DomainEventPublisher
                .instance()
                .publish(new GroupAssignedToRole(
                        this.tenantId(),
                        this.name(),
                        group.name()));
    }

    public void assignUser(final User user) {
        this.assertArgumentNotNull(user, "User must not be null.");
        this.assertArgumentEquals(this.tenantId(), user.tenantId(), "Wrong tenant for this user.");

        this.group().addUser(user);

        // Note: Consider what a consuming Bounded Context would
        // need to do if this event was not enriched with the
        // last three user person properties (Hint: A lot).
        DomainEventPublisher
                .instance()
                .publish(new UserAssignedToRole(
                        this.tenantId(),
                        this.name(),
                        user.username(),
                        user.person().name().firstName(),
                        user.person().name().lastName(),
                        user.person().emailAddress().address()));
    }

    public String description() {
        return description;
    }

    public boolean isInRole(final User user, final GroupMemberService groupMemberService) {
        return this.group().isMember(user, groupMemberService);
    }

    public String name() {
        return name;
    }

    public boolean supportsNesting() {
        return this.supportsNesting;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public void unassignGroup(final Group group) {
        this.assertStateTrue(this.supportsNesting(), "This role does not support group nesting.");
        this.assertArgumentNotNull(group, "Group must not be null.");
        this.assertArgumentEquals(this.tenantId(), group.tenantId(), "Wrong tenant for this group.");

        this.group().removeGroup(group);

        DomainEventPublisher
                .instance()
                .publish(new GroupUnassignedFromRole(
                        this.tenantId(),
                        this.name(),
                        group.name()));
    }

    public void unassignUser(final User user) {
        this.assertArgumentNotNull(user, "User must not be null.");
        this.assertArgumentEquals(this.tenantId(), user.tenantId(), "Wrong tenant for this user.");

        this.group().removeUser(user);

        DomainEventPublisher
                .instance()
                .publish(new UserUnassignedFromRole(
                        this.tenantId(),
                        this.name(),
                        user.username()));
    }

    protected void createInternalGroup() {
        final String groupName =
                Group.ROLE_GROUP_PREFIX
                        + UUID.randomUUID().toString().toUpperCase();

        this.setGroup(new Group(
                this.tenantId(),
                groupName,
                "Role backing group for: " + this.name()));
    }

    protected Group group() {
        return group;
    }

    protected void setGroup(final Group group) {
        this.group = group;
    }

    protected void setTenantId(final TenantId tenantId) {
        this.assertArgumentNotNull(tenantId, "TenantId must be provided.");

        this.tenantId = tenantId;
    }

    protected void setName(final String name) {
        this.assertArgumentNotEmpty(name, "Role name must be provided.");
        this.assertArgumentLength(name, 1, 100, "Role name must be 100 characters or less.");

        this.name = name;
    }

    protected void setDescription(final String description) {
        this.assertArgumentNotEmpty(description, "Role description must be provided.");
        this.assertArgumentLength(description, 1, 250, "Role description must be 250 characters or less.");

        this.description = description;
    }

    protected void setSupportsNesting(final boolean supportsNesting) {
        this.supportsNesting = supportsNesting;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Role role = (Role) o;
        return tenantId.equals(role.tenantId) &&
                name.equals(role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, name);
    }

    protected Role() {
        // Needed by Hibernate.
    }
}
