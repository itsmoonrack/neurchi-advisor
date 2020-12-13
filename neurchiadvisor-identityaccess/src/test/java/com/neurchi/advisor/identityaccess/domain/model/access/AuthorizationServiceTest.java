package com.neurchi.advisor.identityaccess.domain.model.access;

import com.neurchi.advisor.identityaccess.domain.model.IdentityAccessTest;
import com.neurchi.advisor.identityaccess.domain.model.identity.Tenant;
import com.neurchi.advisor.identityaccess.domain.model.identity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthorizationServiceTest extends IdentityAccessTest {

    @Test
    public void TestUserInRoleAuthorization() {
        Tenant tenant = this.tenantAggregate();
        User user = this.userAggregate();
        userRepository().add(user);
        Role managerRole = tenant.provisionRole("Manager", "A manager role.", true);

        managerRole.assignUser(user);

        roleRepository().add(managerRole);

        assertTrue(authorizationService().isUserInRole(user, "Manager"));
        assertFalse(authorizationService().isUserInRole(user, "Director"));
    }

    @Test
    public void TestUsernameInRoleAuthorization() {
        Tenant tenant = this.tenantAggregate();
        User user = this.userAggregate();
        userRepository().add(user);
        Role managerRole = tenant.provisionRole("Manager", "A manager role.", true);

        managerRole.assignUser(user);

        roleRepository().add(managerRole);

        assertTrue(authorizationService().isUserInRole(tenant.tenantId(), user.username(), "Manager"));
        assertFalse(authorizationService().isUserInRole(tenant.tenantId(), user.username(), "Director"));
    }
}
