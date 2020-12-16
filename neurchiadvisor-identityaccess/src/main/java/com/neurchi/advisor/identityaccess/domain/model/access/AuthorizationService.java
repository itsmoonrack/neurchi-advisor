package com.neurchi.advisor.identityaccess.domain.model.access;

import com.neurchi.advisor.common.AssertionConcern;
import com.neurchi.advisor.identityaccess.domain.model.identity.*;

public final class AuthorizationService extends AssertionConcern {

    private final GroupRepository groupRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public AuthorizationService(
            final UserRepository userRepository,
            final GroupRepository groupRepository,
            final RoleRepository roleRepository) {

        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
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
