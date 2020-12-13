package com.neurchi.advisor.identityaccess.domain.model;

import com.neurchi.advisor.identityaccess.domain.model.access.AuthorizationService;
import com.neurchi.advisor.identityaccess.domain.model.access.RoleRepository;
import com.neurchi.advisor.identityaccess.domain.model.identity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.time.LocalDateTime;

public abstract class IdentityAccessTest extends DomainTest {

    protected static final String FIXTURE_TENANT_DESCRIPTION = "This is a test tenant.";
    protected static final String FIXTURE_TENANT_NAME = "Test Tenant";
    protected static final String FIXTURE_USER_EMAIL_ADDRESS = "sbernard@neurchiadvisor.com";
    protected static final String FIXTURE_USER_EMAIL_ADDRESS2 = "nbernard@neurchiadvisor.com";
    protected static final String FIXTURE_USERNAME = "sbernard";
    protected static final String FIXTURE_USERNAME2 = "nberjaud";

    @Autowired
    private ApplicationContext applicationContext;
    private Tenant tenant;

    protected ContactInformation contactInformation() {
        return new ContactInformation(
                new EmailAddress(FIXTURE_USER_EMAIL_ADDRESS));
    }

    protected AccessToken obtainExtendedAccessToken() {
        return new AccessToken(
                "EAAEGYwuZC82MBANgMbDswc6ZAvyZA4RKFc2PKV9k2hEytMnxqiK9ZBCOzQjR1ZC2o4anAETbF1wH3ZC7cWgzAewsUNulmQS66VNlzj9lKCfZAyYBGwF3ipLZBCUREfyAmXhgjKZAHSHnXY7F7OFlMuZC3qsx4TXOapzGZCXnmFMNlFvHtqZAPSGyfPNF",
                "bearer",
                LocalDateTime.now().plusDays(90));
    }

    protected Person personEntity(final Tenant tenant) {
        return new Person(
                tenant.tenantId(),
                new FullName("Sylvain", "Bernard"),
                this.contactInformation());
    }

    protected RegistrationInvitation registrationInvitationEntity(final Tenant tenant) {

        return tenant.offerRegistrationInvitation("Today-and-Tomorrow: " + LocalDateTime.now().minusNanos(1))
                .startingOn(LocalDateTime.now().minusNanos(1))
                .until(LocalDateTime.now().plusDays(1));
    }

    protected Tenant tenantAggregate() {

        if (this.tenant == null) {
            TenantId tenantId =
                    this.tenantRepository().nextIdentity();

            this.tenant =
                    new Tenant(
                            tenantId,
                            FIXTURE_TENANT_NAME,
                            FIXTURE_TENANT_DESCRIPTION,
                            true);

            this.tenantRepository().add(tenant);
        }

        return this.tenant;
    }

    protected Person personEntity2(final Tenant tenant) {
        return new Person(
                tenant.tenantId(),
                new FullName("Noelie", "Berjaud"),
                new ContactInformation(
                        new EmailAddress(FIXTURE_USER_EMAIL_ADDRESS2)));
    }

    protected User userAggregate() {
        Tenant tenant = this.tenantAggregate();

        RegistrationInvitation registrationInvitation =
                this.registrationInvitationEntity(tenant);

        return tenant.registerUser(
                registrationInvitation.invitationId(),
                FIXTURE_USERNAME,
                this.obtainExtendedAccessToken(),
                new Enablement(true, null, null),
                this.personEntity(tenant))
                .orElse(null);
    }

    protected User userAggregate2() {
        Tenant tenant = this.tenantAggregate();

        RegistrationInvitation registrationInvitation =
                this.registrationInvitationEntity(tenant);

        return tenant.registerUser(
                registrationInvitation.invitationId(),
                FIXTURE_USERNAME2,
                this.obtainExtendedAccessToken(),
                new Enablement(true, null, null),
                this.personEntity2(tenant))
                .orElse(null);
    }

    protected LocalDateTime dayBeforeYesterday() {
        return LocalDateTime.now().minusDays(2);
    }

    protected LocalDateTime yesterday() {
        return LocalDateTime.now().minusDays(1);
    }

    protected LocalDateTime today() {
        return LocalDateTime.now().minusNanos(1);
    }

    protected LocalDateTime tomorrow() {
        return LocalDateTime.now().plusDays(1);
    }

    protected LocalDateTime dayAfterTomorrow() {
        return LocalDateTime.now().plusDays(2);
    }

    protected AuthorizationService authorizationService() {
        return new AuthorizationService(this.userRepository(), this.groupRepository(), this.roleRepository());
    }

    protected UserRepository userRepository() {
        return applicationContext.getBean(UserRepository.class);
    }

    protected TenantRepository tenantRepository() {
        return applicationContext.getBean(TenantRepository.class);
    }

    protected GroupRepository groupRepository() {
        return applicationContext.getBean(GroupRepository.class);
    }

    protected RoleRepository roleRepository() {
        return applicationContext.getBean(RoleRepository.class);
    }

    protected GroupMemberService groupMemberService() {
        return new GroupMemberService(this.userRepository(), this.groupRepository());
    }

    protected TenantProvisioningService tenantProvisioningService() {
        return applicationContext.getBean(TenantProvisioningService.class);
    }
}
