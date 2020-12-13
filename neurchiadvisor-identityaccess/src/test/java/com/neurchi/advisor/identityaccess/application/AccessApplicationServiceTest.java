package com.neurchi.advisor.identityaccess.application;

import com.neurchi.advisor.identityaccess.application.command.AssignUserToRoleCommand;
import com.neurchi.advisor.identityaccess.domain.model.access.Role;
import com.neurchi.advisor.identityaccess.domain.model.identity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccessApplicationServiceTest extends ApplicationServiceTest {

    @Test
    public void TestAssignUserToRole() {
        User user = this.userAggregate();
        userRepository().add(user);

        Role role = this.roleAggregate();
        roleRepository().add(role);

        assertFalse(role.isInRole(user, groupMemberService()));

        this
                .accessApplicationService()
                .assignUserToRole(
                        new AssignUserToRoleCommand(
                                user.tenantId().id(),
                                user.username(),
                                role.name()));

        assertTrue(role.isInRole(user, groupMemberService()));
    }

    @Test
    public void TestIsUserInRole() {
        User user = this.userAggregate();
        userRepository().add(user);

        Role role = this.roleAggregate();
        roleRepository().add(role);

        assertFalse(
                this
                        .accessApplicationService()
                        .isUserInRole(
                                user.tenantId().id(),
                                user.username(),
                                role.name()));

        this
                .accessApplicationService()
                .assignUserToRole(
                        new AssignUserToRoleCommand(
                                user.tenantId().id(),
                                user.username(),
                                role.name()));

        assertTrue(
                this
                        .accessApplicationService()
                        .isUserInRole(
                                user.tenantId().id(),
                                user.username(),
                                role.name()));
    }

    @Test
    public void TestUserInRole() {
        User user = this.userAggregate();
        userRepository().add(user);

        Role role = this.roleAggregate();
        roleRepository().add(role);

        User userNotInRole =
                this
                        .accessApplicationService()
                        .userInRole(user.tenantId().id(), user.username(), role.name())
                        .orElse(null);

        assertNull(userNotInRole);

        this
                .accessApplicationService()
                .assignUserToRole(
                        new AssignUserToRoleCommand(
                                user.tenantId().id(),
                                user.username(),
                                role.name()));

        User userInRole =
                this
                        .accessApplicationService()
                        .userInRole(user.tenantId().id(), user.username(), role.name())
                        .orElse(null);

        assertNotNull(userInRole);
    }
}