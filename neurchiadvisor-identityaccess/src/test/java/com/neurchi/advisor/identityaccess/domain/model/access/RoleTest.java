package com.neurchi.advisor.identityaccess.domain.model.access;

import com.neurchi.advisor.common.domain.model.DomainEventPublisher;
import com.neurchi.advisor.common.domain.model.DomainEventSubscriber;
import com.neurchi.advisor.identityaccess.domain.model.IdentityAccessTest;
import com.neurchi.advisor.identityaccess.domain.model.identity.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest extends IdentityAccessTest {

    private int groupSomethingAddedCount;
    private int groupSomethingRemovedCount;
    private int roleSomethingAssignedCount;
    private int roleSomethingUnassignedCount;

    @Test
    public void TestProvisionRole() {
        Tenant tenant = this.tenantAggregate();
        Role role = tenant.provisionRole("Manager", "A manager role.");
        roleRepository().add(role);
        assertEquals(1, roleRepository().allRoles(tenant.tenantId()).count());
    }

    @Test
    public void TestRoleUniqueness() {
        Tenant tenant = this.tenantAggregate();
        Role role1 = tenant.provisionRole("Manager", "A manager role.");
        roleRepository().add(role1);


        Role role2 = tenant.provisionRole("Manager", "A manager role.");
        assertThrows(IllegalStateException.class, () -> roleRepository().add(role2));
    }

    @Test
    public void TestUserIsInRole() {
        Tenant tenant = this.tenantAggregate();
        User user = this.userAggregate();
        userRepository().add(user);
        Role managerRole = tenant.provisionRole("Manager", "A manager role.", true);
        Group group = new Group(user.tenantId(), "Managers", "A group of managers.");
        groupRepository().add(group);
        managerRole.assignGroup(group, groupMemberService());
        roleRepository().add(managerRole);
        group.addUser(user);

        assertTrue(group.isMember(user, groupMemberService()));
        assertTrue(managerRole.isInRole(user, groupMemberService()));
    }

    @Test
    public void TestUserIsNotInRole() {
        Tenant tenant = this.tenantAggregate();
        User user = this.userAggregate();
        userRepository().add(user);
        Role managerRole = tenant.provisionRole("Manager", "A manager role.", true);
        Group group = tenant.provisionGroup("Managers", "A group of managers.");
        groupRepository().add(group);
        managerRole.assignGroup(group, groupMemberService());
        roleRepository().add(managerRole);
        Role accountantRole = new Role(user.tenantId(), "Accountant", "An accountant role.");
        roleRepository().add(accountantRole);

        assertFalse(managerRole.isInRole(user, groupMemberService()));
        assertFalse(accountantRole.isInRole(user, groupMemberService()));
    }

    @Test
    public void TestNoRoleInternalGroupsInFindGroupByName() {
        Tenant tenant = this.tenantAggregate();
        Role roleA = tenant.provisionRole("RoleA", "A role of A.");
        roleRepository().add(roleA);

        assertThrows(IllegalArgumentException.class, () -> groupRepository().groupNamed(tenant.tenantId(), roleA.group().name()));
    }

    @Test
    public void TestInternalGroupAddedEventsNotPublished() {

        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<GroupAssignedToRole>() {
                    @Override
                    public void handleEvent(GroupAssignedToRole aDomainEvent) {
                        ++roleSomethingAssignedCount;
                    }

                    @Override
                    public Class<GroupAssignedToRole> subscribedToType() {
                        return GroupAssignedToRole.class;
                    }
                });
        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<GroupGroupAdded>() {
                    @Override
                    public void handleEvent(GroupGroupAdded aDomainEvent) {
                        ++groupSomethingAddedCount;
                    }

                    @Override
                    public Class<GroupGroupAdded> subscribedToType() {
                        return GroupGroupAdded.class;
                    }
                });
        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<UserAssignedToRole>() {
                    @Override
                    public void handleEvent(UserAssignedToRole aDomainEvent) {
                        ++roleSomethingAssignedCount;
                    }

                    @Override
                    public Class<UserAssignedToRole> subscribedToType() {
                        return UserAssignedToRole.class;
                    }
                });
        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<GroupUserAdded>() {
                    @Override
                    public void handleEvent(GroupUserAdded aDomainEvent) {
                        ++groupSomethingAddedCount;
                    }

                    @Override
                    public Class<GroupUserAdded> subscribedToType() {
                        return GroupUserAdded.class;
                    }
                });

        Tenant tenant = this.tenantAggregate();
        User user = this.userAggregate();
        userRepository().add(user);
        Role managerRole = tenant.provisionRole("Manager", "A manager role.", true);
        Group group = new Group(user.tenantId(), "Managers", "A group of managers.");
        groupRepository().add(group);
        managerRole.assignGroup(group, groupMemberService());
        managerRole.assignUser(user);
        roleRepository().add(managerRole);
        group.addUser(user); // legal add

        assertEquals(2, roleSomethingAssignedCount);
        assertEquals(1, groupSomethingAddedCount);
    }

    @Test
    public void TestInternalGroupRemovedEventsNotPublished() {

        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<GroupAssignedToRole>() {
                    @Override
                    public void handleEvent(GroupAssignedToRole aDomainEvent) {
                        ++roleSomethingAssignedCount;
                    }

                    @Override
                    public Class<GroupAssignedToRole> subscribedToType() {
                        return GroupAssignedToRole.class;
                    }
                });
        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<GroupGroupAdded>() {
                    @Override
                    public void handleEvent(GroupGroupAdded aDomainEvent) {
                        ++groupSomethingAddedCount;
                    }

                    @Override
                    public Class<GroupGroupAdded> subscribedToType() {
                        return GroupGroupAdded.class;
                    }
                });
        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<UserAssignedToRole>() {
                    @Override
                    public void handleEvent(UserAssignedToRole aDomainEvent) {
                        ++roleSomethingAssignedCount;
                    }

                    @Override
                    public Class<UserAssignedToRole> subscribedToType() {
                        return UserAssignedToRole.class;
                    }
                });
        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<GroupUserAdded>() {
                    @Override
                    public void handleEvent(GroupUserAdded aDomainEvent) {
                        ++groupSomethingAddedCount;
                    }

                    @Override
                    public Class<GroupUserAdded> subscribedToType() {
                        return GroupUserAdded.class;
                    }
                });

        Tenant tenant = this.tenantAggregate();
        User user = this.userAggregate();
        userRepository().add(user);
        Role managerRole = tenant.provisionRole("Manager", "A manager role.", true);
        Group group = new Group(user.tenantId(), "Managers", "A group of managers.");
        groupRepository().add(group);
        managerRole.assignUser(user);
        managerRole.assignGroup(group, groupMemberService());
        roleRepository().add(managerRole);

        managerRole.unassignUser(user);
        managerRole.unassignGroup(group);

        assertEquals(2, roleSomethingAssignedCount);
        assertEquals(0, groupSomethingAddedCount);
    }
}
