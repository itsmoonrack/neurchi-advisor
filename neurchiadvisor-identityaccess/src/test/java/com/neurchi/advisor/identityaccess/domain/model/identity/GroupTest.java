package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.DomainEventPublisher;
import com.neurchi.advisor.common.domain.model.DomainEventSubscriber;
import com.neurchi.advisor.identityaccess.domain.model.IdentityAccessTest;
import com.neurchi.advisor.identityaccess.domain.model.access.Role;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GroupTest extends IdentityAccessTest {

    private int groupGroupAddedCount;
    private int groupGroupRemovedCount;
    private int groupUserAddedCount;
    private int groupUserRemovedCount;

    public void TestCacheHitData() {

        TenantId tenantId = new TenantId("FE46D323-612F-41ED-A012-21243525B3B8");

        Tenant tenant = tenantRepository().tenantOfId(tenantId).orElseThrow();

        Role role = roleRepository().roleNamed(tenantId, "TestCache").orElseThrow();
//                tenant.provisionRole("TestCache", "This is a test role.");
//        roleRepository().add(role);

//        for (int index = 20010; index < 25000; index++) {
//
//            User user = userRepository().userWithUsername(tenantId, "test" + index).orElseThrow();
//
//            role.assignUser(user);
//
//            LoggerFactory.getLogger(GroupTest.class).info("User assigned to role " + user.username());
//        }

        User user = userRepository().userWithUsername(tenantId, "test10000").orElseThrow();
        role.unassignUser(user);
    }

    @Test
    public void TestProvisionGroup() {

        Tenant tenant = this.tenantAggregate();

        Group groupA = tenant.provisionGroup("GroupA", "A group named GroupA");

        this.groupRepository().add(groupA);

        assertEquals(1, this.groupRepository().allGroups(tenant.tenantId()).count());
    }

    @Test
    public void TestAddGroup() {

        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<GroupGroupAdded>() {
                    @Override
                    public void handleEvent(final GroupGroupAdded domainEvent) {
                        ++groupGroupAddedCount;
                    }

                    @Override
                    public Class<GroupGroupAdded> subscribedToType() {
                        return GroupGroupAdded.class;
                    }
                });

        Tenant tenant = this.tenantAggregate();
        Group groupA = tenant.provisionGroup("GroupA", "A group named GroupA");
        groupRepository().add(groupA);
        Group groupB = tenant.provisionGroup("GroupB", "A group named GroupB");
        groupRepository().add(groupB);

        groupA.addGroup(groupB, this.groupMemberService());

        assertEquals(1, groupA.groupMembers().size());
        assertEquals(0, groupB.groupMembers().size());
        assertEquals(1, groupGroupAddedCount);
    }

    @Test
    public void TestRequests() {
        TenantId tenantId = new TenantId("FE46D323-612F-41ED-A012-21243525B3B8");
        Tenant tenant = this.tenantRepository().tenantOfId(tenantId).orElseThrow();

        Group group = this.groupRepository().groupNamed(tenantId, "GroupA").orElseThrow();
        User user = this.userRepository().userWithUsername(tenantId, "test11").orElseThrow();

//        for (int index = 10000; index < 100000; index++) {
//            RegistrationInvitation registrationInvitation =
//                    this.registrationInvitationEntity(tenant);
//
//            User user = tenant.registerUser(
//                    registrationInvitation.invitationId(),
//                    "test" + index,
//                    new AccessToken("token", "bearer", LocalDateTime.now().plusDays(90)),
//                    Enablement.indefiniteEnablement(),
//                    this.personEntity(tenant)).orElseThrow();
//
//            this.userRepository().add(user);
//
//            tenant.withdrawInvitation(registrationInvitation.invitationId());
//
////            User user = this.userRepository().userWithUsername(tenantId, "sbernard" + index).orElseThrow();
//            group.addUser(user);
//            LoggerFactory.getLogger(GroupTest.class).info("test" + index);
//        }
//        Group groupB = this.groupRepository().groupNamed(tenantId, "GroupB").orElseThrow();
//        group.addGroup(groupB, this.groupMemberService());

//        group.removeUser(user);
//        group.removeGroup(groupB);
    }

    @Test
    public void TestAddUser() {

        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<GroupUserAdded>() {
                    @Override
                    public void handleEvent(final GroupUserAdded domainEvent) {
                        ++groupUserAddedCount;
                    }

                    @Override
                    public Class<GroupUserAdded> subscribedToType() {
                        return GroupUserAdded.class;
                    }
                });

        Tenant tenant = this.tenantAggregate();
        Group groupA = tenant.provisionGroup("GroupA", "A group named GroupA");
        User user = this.userAggregate();
        userRepository().add(user);
        groupA.addUser(user);
        groupRepository().add(groupA);

        assertEquals(1, groupA.groupMembers().size());
        assertTrue(groupA.isMember(user, groupMemberService()));
        assertEquals(1, groupUserAddedCount);
    }

    @Test
    public void TestRemoveGroup() {

        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<GroupGroupRemoved>() {
                    @Override
                    public void handleEvent(final GroupGroupRemoved domainEvent) {
                        ++groupGroupRemovedCount;
                    }

                    @Override
                    public Class<GroupGroupRemoved> subscribedToType() {
                        return GroupGroupRemoved.class;
                    }
                });

        Tenant tenant = this.tenantAggregate();
        Group groupA = tenant.provisionGroup("GroupA", "A group named GroupA");
        groupRepository().add(groupA);
        Group groupB = tenant.provisionGroup("GroupB", "A group named GroupB");
        groupRepository().add(groupB);
        groupA.addGroup(groupB, groupMemberService());

        assertEquals(1, groupA.groupMembers().size());
        groupA.removeGroup(groupB);
        assertEquals(0, groupA.groupMembers().size());
        assertEquals(1, groupGroupRemovedCount);
    }

    @Test
    public void TestRemoveUser() {

        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<GroupUserRemoved>() {
                    @Override
                    public void handleEvent(final GroupUserRemoved domainEvent) {
                        ++groupUserRemovedCount;
                    }

                    @Override
                    public Class<GroupUserRemoved> subscribedToType() {
                        return GroupUserRemoved.class;
                    }
                });

        Tenant tenant = this.tenantAggregate();
        Group groupA = tenant.provisionGroup("GroupA", "A group named GroupA");
        User user = this.userAggregate();
        userRepository().add(user);
        groupA.addUser(user);
        groupRepository().add(groupA);

        assertEquals(1, groupA.groupMembers().size());
        groupA.removeUser(user);
        assertEquals(0, groupA.groupMembers().size());
        assertEquals(1, groupUserRemovedCount);
    }

    @Test
    public void TestRemoveGroupReferencedUser() {
        Tenant tenant = this.tenantAggregate();
        Group groupA = tenant.provisionGroup("GroupA", "A group named GroupA");
        User user = this.userAggregate();
        userRepository().add(user);
        groupA.addUser(user);
        groupRepository().add(groupA);

        assertEquals(1, groupA.groupMembers().size());
        assertTrue(groupA.isMember(user, groupMemberService()));

        userRepository().remove(user);
        this.session().flush();
        this.session().evict(groupA);

        Group reGrouped = groupRepository()
                .groupNamed(tenant.tenantId(), "GroupA")
                .orElse(null);

        assertNotNull(reGrouped);
        assertEquals("GroupA", reGrouped.name());
        assertEquals(1, reGrouped.groupMembers().size());
        assertFalse(reGrouped.isMember(user, groupMemberService()));
    }

    @Test
    public void TestRepositoryRemoveGroup() {
        Tenant tenant = this.tenantAggregate();
        Group groupA = tenant.provisionGroup("GroupA", "A group named GroupA");
        groupRepository().add(groupA);

        Optional<Group> notNullGroup =
                groupRepository()
                        .groupNamed(tenant.tenantId(), "GroupA");
        assertTrue(notNullGroup.isPresent());

        groupRepository().remove(groupA);

        Optional<Group> nullGroup =
                groupRepository()
                        .groupNamed(tenant.tenantId(), "GroupA");
        assertTrue(nullGroup.isEmpty());
    }

    @Test
    public void TestUserIsMemberOfNestedGroup() {

        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<GroupGroupAdded>() {
                    @Override
                    public void handleEvent(final GroupGroupAdded domainEvent) {
                        ++groupGroupAddedCount;
                    }

                    @Override
                    public Class<GroupGroupAdded> subscribedToType() {
                        return GroupGroupAdded.class;
                    }
                });

        Tenant tenant = this.tenantAggregate();
        Group groupA = tenant.provisionGroup("GroupA", "A group named GroupA");
        groupRepository().add(groupA);
        Group groupB = tenant.provisionGroup("GroupB", "A group named GroupB");
        groupRepository().add(groupB);
        groupA.addGroup(groupB, groupMemberService());
        User user = this.userAggregate();
        userRepository().add(user);
        groupB.addUser(user);

        assertTrue(groupB.isMember(user, groupMemberService()));
        assertTrue(groupA.isMember(user, groupMemberService()));

        assertEquals(1, groupGroupAddedCount);
    }

    @Test
    public void TestUserIsNotMember() {
        User user = this.userAggregate();
        userRepository().add(user);
        // tests alternate creation via constructor
        Group groupA = new Group(user.tenantId(), "GroupA", "A group named GroupA");
        groupRepository().add(groupA);
        Group groupB = new Group(user.tenantId(), "GroupB", "A group named GroupB");
        groupRepository().add(groupB);

        groupA.addGroup(groupB, groupMemberService());

        assertFalse(groupB.isMember(user, groupMemberService()));
        assertFalse(groupA.isMember(user, groupMemberService()));
    }

    @Test
    public void TestNoRecursiveGroupings() {

        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<GroupGroupAdded>() {
                    @Override
                    public void handleEvent(final GroupGroupAdded domainEvent) {
                        ++groupGroupAddedCount;
                    }

                    @Override
                    public Class<GroupGroupAdded> subscribedToType() {
                        return GroupGroupAdded.class;
                    }
                });

        User user = this.userAggregate();
        userRepository().add(user);
        // tests alternate creation via constructor
        Group groupA = new Group(user.tenantId(), "GroupA", "A group named GroupA");
        groupRepository().add(groupA);
        Group groupB = new Group(user.tenantId(), "GroupB", "A group named GroupB");
        groupRepository().add(groupB);
        Group groupC = new Group(user.tenantId(), "GroupC", "A group named GroupC");
        groupRepository().add(groupC);

        groupA.addGroup(groupB, groupMemberService());
        groupB.addGroup(groupC, groupMemberService());

        assertThrows(IllegalArgumentException.class, () -> groupC.addGroup(groupA, groupMemberService()));
        assertEquals(2, groupGroupAddedCount);
    }

    @Test
    public void TestNoRoleInternalGroupsInFindAllGroups() {
        Tenant tenant = this.tenantAggregate();
        Group groupA = tenant.provisionGroup("GroupA", "A group named GroupA");
        groupRepository().add(groupA);

        Role roleA = tenant.provisionRole("RoleA", "A role of A.");
        roleRepository().add(roleA);
        Role roleB = tenant.provisionRole("RoleB", "A role of B.");
        roleRepository().add(roleB);
        Role roleC = tenant.provisionRole("RoleC", "A role of C.");
        roleRepository().add(roleC);

        Stream<Group> groups = groupRepository().allGroups(tenant.tenantId());

        assertEquals(1, groups.count());
    }
}
