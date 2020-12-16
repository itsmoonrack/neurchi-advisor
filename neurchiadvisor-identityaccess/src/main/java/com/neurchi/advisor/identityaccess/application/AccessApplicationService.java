package com.neurchi.advisor.identityaccess.application;

import com.neurchi.advisor.identityaccess.application.command.AssignUserToRoleCommand;
import com.neurchi.advisor.identityaccess.domain.model.access.RoleRepository;
import com.neurchi.advisor.identityaccess.domain.model.identity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class AccessApplicationService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private GroupRepository groupRepository;

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
