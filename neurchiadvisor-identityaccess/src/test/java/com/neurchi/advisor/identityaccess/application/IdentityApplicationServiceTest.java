package com.neurchi.advisor.identityaccess.application;

import com.neurchi.advisor.identityaccess.application.command.*;
import com.neurchi.advisor.identityaccess.domain.model.identity.Group;
import com.neurchi.advisor.identityaccess.domain.model.identity.Tenant;
import com.neurchi.advisor.identityaccess.domain.model.identity.User;
import com.neurchi.advisor.identityaccess.domain.model.identity.UserDescriptor;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class IdentityApplicationServiceTest extends ApplicationServiceTest {

    @Test
    public void TestActivateTenant() {
        Tenant tenant = this.tenantAggregate();
        tenant.deactivate();
        assertFalse(tenant.isActive());

        this
                .identityApplicationService()
                .activateTenant(new ActivateTenantCommand(tenant.tenantId().id()));

        Tenant changedTenant = this.tenantRepository().tenantOfId(tenant.tenantId()).orElse(null);

        assertNotNull(changedTenant);
        assertEquals(tenant.name(), changedTenant.name());
        assertTrue(changedTenant.isActive());
    }

    @Test
    public void TestAddGroupToGroup() {
        Group parentGroup = this.group1Aggregate();
        groupRepository().add(parentGroup);

        Group childGroup = this.group2Aggregate();
        groupRepository().add(childGroup);

        assertEquals(0, parentGroup.groupMembers().size());

        this
                .identityApplicationService()
                .addGroupToGroup(new AddGroupToGroupCommand(
                        parentGroup.tenantId().id(),
                        parentGroup.name(),
                        childGroup.name()));

        assertEquals(1, parentGroup.groupMembers().size());
    }

    @Test
    public void TestAddUserToGroup() {
        Group parentGroup = this.group1Aggregate();
        groupRepository().add(parentGroup);

        Group childGroup = this.group2Aggregate();
        groupRepository().add(childGroup);

        User user = this.userAggregate();
        userRepository().add(user);

        assertEquals(0, parentGroup.groupMembers().size());
        assertEquals(0, childGroup.groupMembers().size());

        parentGroup.addGroup(childGroup, groupMemberService());

        this
                .identityApplicationService()
                .addUserToGroup(new AddUserToGroupCommand(
                        childGroup.tenantId().id(),
                        childGroup.name(),
                        user.username()));

        assertEquals(1, parentGroup.groupMembers().size());
        assertEquals(1, childGroup.groupMembers().size());
        assertTrue(parentGroup.isMember(user, groupMemberService()));
        assertTrue(childGroup.isMember(user, groupMemberService()));
    }

    @Test
    public void TestDeactivateTenant() {
        Tenant tenant = this.tenantAggregate();
        assertTrue(tenant.isActive());

        this
                .identityApplicationService()
                .deactivateTenant(new DeactivateTenantCommand(tenant.tenantId().id()));

        Tenant changedTenant = tenantRepository().tenantOfId(tenant.tenantId()).orElse(null);

        assertNotNull(changedTenant);
        assertEquals(tenant.name(), changedTenant.name());
        assertFalse(changedTenant.isActive());
    }

    @Test
    public void TestChangeUserContactInformation() {
        User user = this.userAggregate();
        userRepository().add(user);

        this
                .identityApplicationService()
                .changeUserContactInformation(
                        new ChangeContactInfoCommand(
                                user.tenantId().id(),
                                user.username(),
                                "newemailaddress@neurchiadvisor.com"));

        User changedUser =
                userRepository()
                        .userWithUsername(
                                user.tenantId(),
                                user.username())
                        .orElse(null);

        assertNotNull(changedUser);
        assertEquals("newemailaddress@neurchiadvisor.com", changedUser.person().emailAddress().address());
    }

    @Test
    public void TestChangeUserEmailAddress() {
        User user = this.userAggregate();
        userRepository().add(user);

        this
                .identityApplicationService()
                .changeUserEmailAddress(
                        new ChangeEmailAddressCommand(
                                user.tenantId().id(),
                                user.username(),
                                "newemailaddress@neurchiadvisor.com"));

        User changedUser =
                userRepository()
                        .userWithUsername(
                                user.tenantId(),
                                user.username())
                        .orElse(null);

        assertNotNull(changedUser);
        assertEquals("newemailaddress@neurchiadvisor.com", changedUser.person().emailAddress().address());
    }

    @Test
    public void TestChangeUserPersonalName() {
        User user = this.userAggregate();
        userRepository().add(user);

        this
                .identityApplicationService()
                .changeUserPersonalName(
                        new ChangeUserPersonalNameCommand(
                                user.tenantId().id(),
                                user.username(),
                                "Sylvain",
                                "Bernard"));

        User changedUser =
                userRepository()
                        .userWithUsername(
                                user.tenantId(),
                                user.username())
                        .orElse(null);

        assertNotNull(changedUser);
        assertEquals("Sylvain Bernard", changedUser.person().name().asFormattedName());
    }

    @Test
    public void TestDefineUserEnablement() {
        User user = this.userAggregate();
        userRepository().add(user);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime then = now.plusDays(60);

        this
                .identityApplicationService()
                .defineUserEnablement(
                        new DefineUserEnablementCommand(
                                user.tenantId().id(),
                                user.username(),
                                true,
                                now,
                                then));

        User changedUser =
                userRepository()
                        .userWithUsername(
                                user.tenantId(),
                                user.username())
                        .orElse(null);

        assertNotNull(changedUser);
        assertTrue(changedUser.isEnabled());
    }

    @Test
    public void TestIsGroupMember() {
        Group parentGroup = this.group1Aggregate();
        groupRepository().add(parentGroup);

        Group childGroup = this.group2Aggregate();
        groupRepository().add(childGroup);

        User user = this.userAggregate();
        userRepository().add(user);

        assertEquals(0, parentGroup.groupMembers().size());
        assertEquals(0, childGroup.groupMembers().size());

        parentGroup.addGroup(childGroup, groupMemberService());
        childGroup.addUser(user);

        assertTrue(
                this
                        .identityApplicationService()
                        .isGroupMember(
                                parentGroup.tenantId().id(),
                                parentGroup.name(),
                                user.username()));

        assertTrue(
                this
                        .identityApplicationService()
                        .isGroupMember(
                                childGroup.tenantId().id(),
                                childGroup.name(),
                                user.username()));
    }

    @Test
    public void TestRemoveGroupFromGroup() {
        Group parentGroup = this.group1Aggregate();
        groupRepository().add(parentGroup);

        Group childGroup = this.group2Aggregate();
        groupRepository().add(childGroup);

        parentGroup.addGroup(childGroup, groupMemberService());

        assertEquals(1, parentGroup.groupMembers().size());

        this
                .identityApplicationService()
                .removeGroupFromGroup(new RemoveGroupFromGroupCommand(
                        parentGroup.tenantId().id(),
                        parentGroup.name(),
                        childGroup.name()));

        assertEquals(0, parentGroup.groupMembers().size());
    }

    @Test
    public void TestRemoveUserFromGroup() {
        Group parentGroup = this.group1Aggregate();
        groupRepository().add(parentGroup);

        Group childGroup = this.group2Aggregate();
        groupRepository().add(childGroup);

        User user = this.userAggregate();
        userRepository().add(user);

        parentGroup.addGroup(childGroup, groupMemberService());
        childGroup.addUser(user);

        assertEquals(1, parentGroup.groupMembers().size());
        assertEquals(1, childGroup.groupMembers().size());
        assertTrue(parentGroup.isMember(user, groupMemberService()));
        assertTrue(childGroup.isMember(user, groupMemberService()));

        this
                .identityApplicationService()
                .removeUserFromGroup(new RemoveUserFromGroupCommand(
                        childGroup.tenantId().id(),
                        childGroup.name(),
                        user.username()));

        assertEquals(1, parentGroup.groupMembers().size());
        assertEquals(0, childGroup.groupMembers().size());
        assertFalse(parentGroup.isMember(user, groupMemberService()));
        assertFalse(childGroup.isMember(user, groupMemberService()));
    }

    @Test
    public void TestQueryTenant() {
        Tenant tenant = this.tenantAggregate();

        Tenant queriedTenant =
                this
                        .identityApplicationService()
                        .tenant(tenant.tenantId().id())
                        .orElse(null);

        assertNotNull(queriedTenant);
        assertEquals(tenant, queriedTenant);
    }

    @Test
    public void TestQueryUser() {
        User user = this.userAggregate();
        userRepository().add(user);

        User queriedUser =
                this
                        .identityApplicationService()
                        .user(user.tenantId().id(), user.username())
                        .orElse(null);

        assertNotNull(user);
        assertEquals(user, queriedUser);
    }

    @Test
    public void TestQueryUserDescriptor() {
        User user = this.userAggregate();
        userRepository().add(user);

        UserDescriptor queriedUserDescriptor =
                this
                        .identityApplicationService()
                        .userDescriptor(user.tenantId().id(), user.username())
                        .orElse(null);

        assertNotNull(user);
        assertEquals(user.userDescriptor(), queriedUserDescriptor);
    }
}
