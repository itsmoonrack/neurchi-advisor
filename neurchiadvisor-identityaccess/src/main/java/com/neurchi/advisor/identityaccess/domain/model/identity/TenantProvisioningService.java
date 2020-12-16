package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.DomainEventPublisher;
import com.neurchi.advisor.identityaccess.domain.model.access.Role;
import com.neurchi.advisor.identityaccess.domain.model.access.RoleRepository;

public class TenantProvisioningService {

    private final RoleRepository roleRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    public TenantProvisioningService(
            final TenantRepository tenantRepository,
            final UserRepository userRepository,
            final RoleRepository roleRepository) {

        this.roleRepository = roleRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
    }

    public Tenant provisionTenant(
            final String tenantName,
            final String tenantDescription,
            final String administratorUsername,
            final AccessToken administratorAccessToken,
            final FullName administratorName,
            final EmailAddress emailAddress) {

        try {
            final Tenant tenant = new Tenant(
                    this.tenantRepository().nextIdentity(),
                    tenantName,
                    tenantDescription,
                    true);

            this.tenantRepository().add(tenant);

            this.registerAdministratorFor(
                    tenant,
                    administratorUsername,
                    administratorAccessToken,
                    administratorName,
                    emailAddress);

            DomainEventPublisher
                    .instance()
                    .publish(new TenantProvisioned(
                            tenant.tenantId()));

            return tenant;

        } catch (Throwable t) {
            throw new IllegalStateException("Unable to provision tenant", t);
        }
    }

    private void registerAdministratorFor(
            final Tenant tenant,
            final String administratorUsername,
            final AccessToken administratorAccessToken,
            final FullName administratorName,
            final EmailAddress emailAddress) {

        final RegistrationInvitation invitation =
                tenant.offerRegistrationInvitation("init").openEnded();

        final User admin =
                tenant.registerUser(
                        invitation.invitationId(),
                        administratorUsername,
                        administratorAccessToken,
                        Enablement.indefiniteEnablement(),
                        new Person(
                                tenant.tenantId(),
                                administratorName,
                                new ContactInformation(
                                        emailAddress)))
                .orElseThrow();

        tenant.withdrawInvitation(invitation.invitationId());

        this.userRepository().add(admin);

        final Role adminRole =
                tenant.provisionRole(
                        "Administrator",
                        "Default " + tenant.name() + " administrator.");

        adminRole.assignUser(admin);

        this.roleRepository().add(adminRole);

        DomainEventPublisher
                .instance()
                .publish(new TenantAdministratorRegistered(
                        tenant.tenantId(),
                        tenant.name(),
                        administratorName,
                        emailAddress,
                        admin.username()));
    }

    private RoleRepository roleRepository() {
        return this.roleRepository;
    }

    private TenantRepository tenantRepository() {
        return this.tenantRepository;
    }

    private UserRepository userRepository() {
        return this.userRepository;
    }
}
