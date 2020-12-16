package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.ConcurrencySafeEntity;
import com.neurchi.advisor.common.domain.model.DomainEventPublisher;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Group extends ConcurrencySafeEntity {

    public static final String ROLE_GROUP_PREFIX = "ROLE-INTERNAL-GROUP: ";

    private String description;
    private Set<GroupMember> groupMembers;
    private String name;
    private TenantId tenantId;

    public Group(final TenantId tenantId, final String name, final String description) {
        this();

        setDescription(description);
        setName(name);
        setTenantId(tenantId);
    }

    public void addGroup(final Group group, final GroupMemberService groupMemberService) {
        this.assertArgumentNotNull(group, "The group is required.");
        this.assertArgumentEquals(this.tenantId(), group.tenantId(), "Wrong tenant for this group.");
        this.assertArgumentFalse(groupMemberService.isMemberGroup(group, toGroupMember()), "Group recursion.");

        if (this.groupMembers().add(group.toGroupMember()) && !this.isInternalGroup()) {
            DomainEventPublisher
                    .instance()
                    .publish(new GroupGroupAdded(
                            this.tenantId(),
                            this.name(),
                            group.name()));
        }
    }

    public void addUser(final User user) {
        this.assertArgumentNotNull(user, "User must not be null.");
        this.assertArgumentEquals(this.tenantId(), user.tenantId(), "Wrong tenant for this group.");
        this.assertArgumentTrue(user.isEnabled(), "User is not enabled.");

        if (this.groupMembers().add(user.toGroupMember()) && !this.isInternalGroup()) {
            DomainEventPublisher
                    .instance()
                    .publish(new GroupUserAdded(
                            this.tenantId(),
                            this.name(),
                            user.username()));
        }
    }

    public void replaceMembers(final Set<GroupMember> replacementMembers) {
        groupMembers.retainAll(replacementMembers);
        groupMembers.addAll(replacementMembers);
    }

    public boolean isMember(final User user, final GroupMemberService groupMemberService) {
        this.assertArgumentNotNull(user, "User must not be null.");
        this.assertArgumentEquals(this.tenantId(), user.tenantId(), "Wrong tenant for this group.");
        this.assertArgumentTrue(user.isEnabled(), "User is not enabled.");

        boolean isMember = this.groupMembers().contains(user.toGroupMember());

        if (isMember) {
            isMember = groupMemberService.confirmUser(this, user);
        } else {
            isMember = groupMemberService.isUserInNestedGroup(this, user);
        }

        return isMember;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String description() {
        return description;
    }

    public String name() {
        return name;
    }

    public void removeGroup(final Group group) {
        this.assertArgumentNotNull(group, "Group must not be null.");
        this.assertArgumentEquals(this.tenantId(), group.tenantId(), "Wrong tenant for this group.");

        // Not a nested remove, only direct member.
        if (this.groupMembers().remove(group.toGroupMember()) && !this.isInternalGroup()) {
            DomainEventPublisher
                    .instance()
                    .publish(new GroupGroupRemoved(
                            this.tenantId(),
                            this.name(),
                            group.name()));
        }
    }

    public void removeUser(final User user) {
        this.assertArgumentNotNull(user, "User must not be null.");
        this.assertArgumentEquals(this.tenantId(), user.tenantId(), "Wrong tenant for this group.");

        // Not a nested remove, only direct member.
        if (this.groupMembers().remove(user.toGroupMember()) && !this.isInternalGroup()) {
            DomainEventPublisher
                    .instance()
                    .publish(new GroupUserRemoved(
                            this.tenantId(),
                            this.name(),
                            user.username()));
        }
    }

    public Set<GroupMember> groupMembers() {
        return this.groupMembers;
    }

    protected void setTenantId(final TenantId tenantId) {
        this.assertArgumentNotNull(tenantId, "Tenant Id is required.");

        this.tenantId = tenantId;
    }

    protected void setGroupMembers(final Set<GroupMember> groupMembers) {
        this.groupMembers = groupMembers;
    }

    protected boolean isInternalGroup() {
        return this.isInternalGroup(this.name());
    }

    protected boolean isInternalGroup(final String name) {
        return name.startsWith(ROLE_GROUP_PREFIX);
    }

    protected GroupMember toGroupMember() {
        GroupMember groupMember =
                new GroupMember(
                        this.tenantId(),
                        this.name(),
                        GroupMemberType.Group);

        return groupMember;
    }

    protected void setName(final String name) {
        this.assertArgumentNotNull(name, "Group name is required.");
        this.assertArgumentLength(name, 100, "Role name must be 100 characters or less.");

        if (isInternalGroup(name)) {
            final String uuid = name.substring(ROLE_GROUP_PREFIX.length());

            try {
                UUID.fromString(uuid);
            } catch (Exception e) {
                throw new IllegalArgumentException("The group name has an invalid format.");
            }
        }

        this.name = name;
    }

    protected void setDescription(final String description) {
        this.assertArgumentNotNull(description, "Group description is required.");
        this.assertArgumentLength(description, 250, "Role description must be 250 characters or less.");

        this.description = description;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Group group = (Group) o;
        return name.equals(group.name) &&
                tenantId.equals(group.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tenantId);
    }

    protected Group() {
        // Needed by Hibernate.
        this.setGroupMembers(new HashSet<>(0));
    }
}
