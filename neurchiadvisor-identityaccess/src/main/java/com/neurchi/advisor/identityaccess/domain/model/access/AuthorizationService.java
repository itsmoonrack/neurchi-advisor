package com.neurchi.advisor.identityaccess.domain.model.access;

import com.neurchi.advisor.common.AssertionConcern;
import com.neurchi.advisor.identityaccess.domain.model.identity.*;

public final class AuthorizationService extends AssertionConcern {

    private GroupRepository groupRepository;
    private RoleRepository roleRepository;
    private UserRepository userRepository;

    public AuthorizationService(
            UserRepository aUserRepository,
            GroupRepository aGroupRepository,
            RoleRepository aRoleRepository) {

        this.groupRepository = aGroupRepository;
        this.roleRepository = aRoleRepository;
        this.userRepository = aUserRepository;
    }

    public boolean isUserInRole(final TenantId tenantId, final String username, final String roleName) {
        this.assertArgumentNotNull(tenantId, "TenantId must be provided.");
        this.assertArgumentNotEmpty(username, "Username must not be provided.");
        this.assertArgumentNotEmpty(roleName, "Role name must be provided.");

        return this.userRepository()
                .userWithUsername(tenantId, username)
                .map(user -> this.isUserInRole(user, roleName))
                .orElse(false);
    }

    public boolean isUserInRole(final User user, final String roleName) {
        this.assertArgumentNotNull(user, "User must be provided.");
        this.assertArgumentNotEmpty(roleName, "Role name must be provided.");

        if (user.isEnabled()) {
            return this.roleRepository()
                    .roleNamed(user.tenantId(), roleName)
                    .map(role -> role.isInRole(user, new GroupMemberService(this.userRepository(), this.groupRepository())))
                    .orElse(false);
        }

        return false;
    }

    private GroupRepository groupRepository() {
        return this.groupRepository;
    }

    private RoleRepository roleRepository() {
        return this.roleRepository;
    }

    private UserRepository userRepository() {
        return this.userRepository;
    }
}
