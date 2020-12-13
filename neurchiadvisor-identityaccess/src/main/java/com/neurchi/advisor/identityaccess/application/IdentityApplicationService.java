package com.neurchi.advisor.identityaccess.application;

import com.neurchi.advisor.identityaccess.application.command.*;
import com.neurchi.advisor.identityaccess.domain.model.identity.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class IdentityApplicationService {

    private final GroupMemberService groupMemberService;
    private final GroupRepository groupRepository;
    private final TenantProvisioningService tenantProvisioningService;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    IdentityApplicationService(
            final GroupMemberService groupMemberService,
            final GroupRepository groupRepository,
            final TenantProvisioningService tenantProvisioningService,
            final TenantRepository tenantRepository,
            final UserRepository userRepository) {

        this.groupMemberService = groupMemberService;
        this.groupRepository = groupRepository;
        this.tenantProvisioningService = tenantProvisioningService;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void activateTenant(final ActivateTenantCommand command) {
        Tenant tenant = this.existingTenant(command.getTenantId());

        tenant.activate();
    }

    @Transactional
    public void addGroupToGroup(final AddGroupToGroupCommand command) {
        Group parentGroup =
                this.existingGroup(
                        command.getTenantId(),
                        command.getParentGroupName());

        Group childGroup =
                this.existingGroup(
                        command.getTenantId(),
                        command.getChildGroupName());

        parentGroup.addGroup(childGroup, this.groupMemberService());
    }

    @Transactional
    public void addUserToGroup(final AddUserToGroupCommand command) {
        Group group =
                this.existingGroup(
                        command.getTenantId(),
                        command.getGroupName());

        User user =
                this.existingUser(
                        command.getTenantId(),
                        command.getUsername());

        group.addUser(user);
    }

    @Transactional
    public void deactivateTenant(final DeactivateTenantCommand command) {
        Tenant tenant = this.existingTenant(command.getTenantId());

        tenant.deactivate();
    }

    @Transactional
    public void changeUserContactInformation(final ChangeContactInfoCommand command) {
        User user = this.existingUser(command.getTenantId(), command.getUsername());

        this.internalChangeUserContactInformation(
                user,
                new ContactInformation(
                        new EmailAddress(command.getEmailAddress())));
    }

    @Transactional
    public void changeUserEmailAddress(final ChangeEmailAddressCommand command) {
        User user = this.existingUser(command.getTenantId(), command.getUsername());

        this.internalChangeUserContactInformation(
                user,
                user.person()
                    .contactInformation()
                    .changeEmailAddress(new EmailAddress(command.getEmailAddress())));
    }

    @Transactional
    public void extendAccessToken(final ExtendAccessTokenCommand command) {
        User user = this.existingUser(command.getTenantId(), command.getUsername());

        user.extendAccessToken(
                new AccessToken(
                        command.getAccessToken(),
                        command.getTokenType(),
                        command.getExpiresIn()));
    }

    @Transactional
    public void changeUserPersonalName(final ChangeUserPersonalNameCommand command) {
        User user = this.existingUser(command.getTenantId(), command.getUsername());

        user.person().changeName(new FullName(command.getFirstName(), command.getLastName()));
    }

    @Transactional
    public void defineUserEnablement(final DefineUserEnablementCommand command) {
        User user = this.existingUser(command.getTenantId(), command.getUsername());

        user.defineEnablement(
                new Enablement(
                        command.isEnabled(),
                        command.getStartDate(),
                        command.getEndDate()));
    }

    @Transactional(readOnly = true)
    public Optional<Group> group(final String tenantId, final String groupName) {
        return this
                .groupRepository()
                .groupNamed(new TenantId(tenantId), groupName);
    }

    @Transactional(readOnly=true)
    public boolean isGroupMember(final String tenantId, final String groupName, final String username) {
        Group group =
                this.existingGroup(
                        tenantId,
                        groupName);

        User user =
                this.existingUser(
                        tenantId,
                        username);

        return group.isMember(user, this.groupMemberService());
    }

    @Transactional
    public Tenant provisionTenant(final ProvisionTenantCommand command) {

        return this.tenantProvisioningService().provisionTenant(
                command.getTenantName(),
                command.getTenantDescription(),
                command.getAdministratorUsername(),
                new AccessToken(
                        command.getAdministratorAccessToken(),
                        command.getAdministratorTokenType(),
                        command.getAdministratorExpiresIn()),
                new FullName(
                        command.getAdministratorFirstName(),
                        command.getAdministratorLastName()),
                new EmailAddress(
                        command.getEmailAddress()));
    }

    @Transactional
    public User registerUser(final RegisterUserCommand command) {
        Tenant tenant = this.existingTenant(command.getTenantId());

        User user =
                tenant.registerUser(
                        command.getInvitationIdentifier(),
                        command.getUsername(),
                        new AccessToken(
                                command.getAccessToken(),
                                command.getTokenType(),
                                command.getExpiresIn()),
                        new Enablement(
                                command.isEnabled(),
                                command.getStartDate(),
                                command.getEndDate()),
                        new Person(
                                new TenantId(command.getTenantId()),
                                new FullName(command.getFirstName(), command.getLastName()),
                                new ContactInformation(
                                        new EmailAddress(
                                                command.getEmailAddress()))))
                .orElseThrow(() -> new IllegalStateException("User not registered."));

        this.userRepository().add(user);

        return user;
    }

    @Transactional
    public void removeGroupFromGroup(final RemoveGroupFromGroupCommand command) {
        Group parentGroup =
                this.existingGroup(
                        command.getTenantId(),
                        command.getParentGroupName());

        Group childGroup =
                this.existingGroup(
                        command.getTenantId(),
                        command.getChildGroupName());

        parentGroup.removeGroup(childGroup);
    }

    @Transactional
    public void removeUserFromGroup(final RemoveUserFromGroupCommand command) {
        Group group =
                this.existingGroup(
                        command.getTenantId(),
                        command.getGroupName());

        User user =
                this.existingUser(
                        command.getTenantId(),
                        command.getUsername());

        group.removeUser(user);
    }

    @Transactional(readOnly = true)
    public Optional<Tenant> tenant(final String tenantId) {
        return this
                .tenantRepository()
                .tenantOfId(new TenantId(tenantId));
    }

    @Transactional(readOnly = true)
    public Optional<User> user(final String tenantId, final String username) {
        return this
                .userRepository()
                .userWithUsername(new TenantId(tenantId), username);
    }

    @Transactional(readOnly=true)
    public Optional<UserDescriptor> userDescriptor(
            final String tenantId,
            final String username) {
        return this.user(tenantId, username).map(User::userDescriptor);
    }

    private Group existingGroup(final String tenantId, final String groupName) {
        return this
                .group(tenantId, groupName)
                .orElseThrow(() -> new IllegalArgumentException("Group does not exist: " + tenantId + " : " + groupName));
    }

    private Tenant existingTenant(final String tenantId) {
        return this
                .tenant(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant does not exist: " + tenantId));
    }

    private User existingUser(final String tenantId, final String username) {
        return this
                .user(tenantId, username)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist: " + tenantId + " : " + username));
    }

    private void internalChangeUserContactInformation(
            final User user,
            final ContactInformation contactInformation) {

        if (user == null) {
            throw new IllegalArgumentException("User must exist.");
        }

        user.person().changeContactInformation(contactInformation);
    }

    private GroupMemberService groupMemberService() {
        return this.groupMemberService;
    }

    private GroupRepository groupRepository() {
        return this.groupRepository;
    }

    private TenantProvisioningService tenantProvisioningService() {
        return this.tenantProvisioningService;
    }

    private TenantRepository tenantRepository() {
        return this.tenantRepository;
    }

    private UserRepository userRepository() {
        return this.userRepository;
    }
}
