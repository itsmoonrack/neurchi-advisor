package com.neurchi.advisor.identityaccess.application;

import com.neurchi.advisor.identityaccess.application.command.AssignUserToRoleCommand;
import com.neurchi.advisor.identityaccess.domain.model.access.RoleRepository;
import com.neurchi.advisor.identityaccess.domain.model.identity.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccessApplicationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GroupRepository groupRepository;

    AccessApplicationService(
            final UserRepository userRepository,
            final RoleRepository roleRepository,
            final GroupRepository groupRepository) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional
    public void assignUserToRole(final AssignUserToRoleCommand command) {

        TenantId tenantId = new TenantId(command.getTenantId());

        this
                .userRepository()
                .userWithUsername(
                        tenantId,
                        command.getUsername())
                .ifPresent(user -> this
                        .roleRepository()
                        .roleNamed(
                                tenantId,
                                command.getRoleName())
                        .ifPresent(role -> role.assignUser(user)));
    }

    @Transactional(readOnly = true)
    public boolean isUserInRole(
            final String tenantId,
            final String username,
            final String roleName) {

        return this.userInRole(tenantId, username, roleName).isPresent();
    }

    @Transactional(readOnly = true)
    public Optional<User> userInRole(
            final String tenantIdentity,
            final String username,
            final String roleName) {

        final TenantId tenantId = new TenantId(tenantIdentity);

        final GroupMemberService groupMemberService =
                new GroupMemberService(
                        this.userRepository(),
                        this.groupRepository());

        return this.userRepository()
                .userWithUsername(tenantId, username)
                .filter(user -> this.roleRepository()
                        .roleNamed(tenantId, roleName)
                        .filter(role -> role.isInRole(user, groupMemberService))
                        .isPresent());
    }

    private UserRepository userRepository() {
        return this.userRepository;
    }

    private RoleRepository roleRepository() {
        return this.roleRepository;
    }

    private GroupRepository groupRepository() {
        return this.groupRepository;
    }
}
