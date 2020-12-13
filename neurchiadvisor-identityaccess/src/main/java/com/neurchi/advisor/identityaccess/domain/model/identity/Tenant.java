package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.ConcurrencySafeEntity;
import com.neurchi.advisor.common.domain.model.DomainEventPublisher;
import com.neurchi.advisor.identityaccess.domain.model.access.Role;
import com.neurchi.advisor.identityaccess.domain.model.access.RoleProvisioned;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class Tenant extends ConcurrencySafeEntity {

    private boolean active;
    private String description;
    private String name;
    private Set<RegistrationInvitation> registrationInvitations = new HashSet<>();
    private TenantId tenantId;

    public Tenant(final TenantId tenantId, final String name, final String description, final boolean active) {
        this();

        setActive(active);
        setDescription(description);
        setName(name);
        setTenantId(tenantId);
    }

    public void activate() {
        if (!this.isActive()) {

            this.setActive(true);

            DomainEventPublisher
                    .instance()
                    .publish(new TenantActivated(this.tenantId()));
        }
    }

    public Stream<InvitationDescriptor> allAvailableRegistrationInvitations() {
        this.assertStateTrue(this.isActive(), "Tenant is not active.");

        return this.allRegistrationInvitationsFor(true);
    }

    public Stream<InvitationDescriptor> allUnavailableRegistrationInvitations() {
        this.assertStateTrue(this.isActive(), "Tenant is not active.");

        return this.allRegistrationInvitationsFor(false);
    }

    public void deactivate() {
        if (this.isActive()) {

            this.setActive(false);

            DomainEventPublisher
                    .instance()
                    .publish(new TenantDeactivated(this.tenantId()));
        }
    }

    public String description() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isRegistrationAvailableThrough(final String invitationIdentifier) {
        this.assertStateTrue(this.isActive(), "Tenant is not active.");

        return this
                .invitation(invitationIdentifier)
                .filter(RegistrationInvitation::isAvailable)
                .isPresent();
    }

    public String name() {
        return name;
    }

    public RegistrationInvitation offerRegistrationInvitation(final String description) {
        this.assertStateTrue(this.isActive(), "Tenant is not active.");
        this.assertStateFalse(isRegistrationAvailableThrough(description), "Invitation already exists.");

        final RegistrationInvitation invitation =
                new RegistrationInvitation(
                        this.tenantId(),
                        UUID.randomUUID().toString().toUpperCase(),
                        description);

        final boolean added = registrationInvitations.add(invitation);

        this.assertStateTrue(added, "The invitation should have been added.");

        return invitation;
    }

    public Group provisionGroup(final String name, final String description) {
        this.assertStateTrue(this.isActive(), "Tenant is not active.");

        final Group group = new Group(this.tenantId(), name, description);

        DomainEventPublisher
                .instance()
                .publish(new GroupProvisioned(
                        this.tenantId(),
                        name));

        return group;
    }

    public Role provisionRole(final String name, final String description) {
        return provisionRole(name, description, false);
    }

    public Role provisionRole(final String name, final String description, final boolean supportsNesting) {
        this.assertStateTrue(this.isActive(), "Tenant is not active.");

        final Role role = new Role(this.tenantId(), name, description, supportsNesting);

        DomainEventPublisher
                .instance()
                .publish(new RoleProvisioned(
                        this.tenantId(),
                        name));

        return role;
    }

    public Optional<RegistrationInvitation> redefineRegistrationInvitationAs(final String invitationIdentifier) {
        this.assertStateTrue(this.isActive(), "Tenant is not active.");

        return this
                .invitation(invitationIdentifier)
                .map(RegistrationInvitation::openEnded);
    }

    public Optional<User> registerUser(
            final String invitationIdentifier,
            final String username,
            final AccessToken accessToken,
            final Enablement enablement,
            final Person person) {

        this.assertStateTrue(this.isActive(), "Tenant is not active.");

        if (this.isRegistrationAvailableThrough(invitationIdentifier)) {
            // Ensures same tenant.
            person.setTenantId(this.tenantId());

            return Optional.of(new User(this.tenantId(), username, accessToken, enablement, person));
        }

        return Optional.empty();
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public void withdrawInvitation(final String invitationIdentifier) {
        invitation(invitationIdentifier).ifPresent(registrationInvitations::remove);
    }

    protected void setActive(final boolean active) {
        this.active = active;
    }

    protected Stream<InvitationDescriptor> allRegistrationInvitationsFor(boolean isAvailable) {
        return this.registrationInvitations()
                .filter(invitation -> invitation.isAvailable() == isAvailable)
                .map(RegistrationInvitation::toDescriptor);
    }

    protected void setDescription(final String description) {
        this.assertArgumentNotEmpty(description, "The tenant description is required.");
        this.assertArgumentLength(description, 1, 100, "The tenant description must be 100 characters or less.");

        this.description = description;
    }

    protected Optional<RegistrationInvitation> invitation(final String invitationIdentifier) {
        return this.registrationInvitations()
                .filter(invitation -> invitation.isIdentifiedBy(invitationIdentifier))
                .findFirst();
    }

    protected void setName(final String name) {
        this.assertArgumentNotEmpty(name, "The tenant name is required.");
        this.assertArgumentLength(name, 1, 100, "The tenant name must be 100 characters or less.");

        this.name = name;
    }

    protected Stream<RegistrationInvitation> registrationInvitations() {
        return registrationInvitations.stream();
    }

    protected void setRegistrationInvitations(final Set<RegistrationInvitation> registrationInvitations) {
        this.registrationInvitations = registrationInvitations;
    }

    protected void setTenantId(final TenantId tenantId) {
        this.assertArgumentNotNull(tenantId, "TenantId is required.");

        this.tenantId = tenantId;
    }

    protected Tenant() {
        // Needed by Hibernate.
        this.setRegistrationInvitations(new HashSet<>(0));
    }
}
