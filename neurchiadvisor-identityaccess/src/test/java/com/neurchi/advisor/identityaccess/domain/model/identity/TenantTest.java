package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.DomainEventPublisher;
import com.neurchi.advisor.common.domain.model.DomainEventSubscriber;
import com.neurchi.advisor.identityaccess.domain.model.IdentityAccessTest;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TenantTest extends IdentityAccessTest {

    private boolean handled1;
    private boolean handled2;

    @Test
    public void TestProvisionTenant() {

        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<TenantProvisioned>() {
                    @Override
                    public void handleEvent(final TenantProvisioned domainEvent) {
                        handled1 = true;
                    }

                    @Override
                    public Class<TenantProvisioned> subscribedToType() {
                        return TenantProvisioned.class;
                    }
                });

        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<TenantAdministratorRegistered>() {
                    @Override
                    public void handleEvent(final TenantAdministratorRegistered domainEvent) {
                        handled2 = true;
                    }

                    @Override
                    public Class<TenantAdministratorRegistered> subscribedToType() {
                        return TenantAdministratorRegistered.class;
                    }
                });

        Tenant tenant =
                this.tenantProvisioningService()
                        .provisionTenant(
                                FIXTURE_TENANT_NAME,
                                FIXTURE_TENANT_DESCRIPTION,
                                "10157329112631392",
                                this.obtainExtendedAccessToken(),
                                new FullName("Sylvain", "Bernard"),
                                new EmailAddress("sylvain@neurchiadvisor.com"));

        assertTrue(handled1);
        assertTrue(handled2);

        assertNotNull(tenant.tenantId());
        assertNotNull(tenant.tenantId().id());
        assertEquals(36, tenant.tenantId().id().length());
        assertEquals(FIXTURE_TENANT_NAME, tenant.name());
        assertEquals(FIXTURE_TENANT_DESCRIPTION, tenant.description());
    }

    @Test
    public void TestCreateOpenEndedInvitation() {

        Tenant tenant = this.tenantAggregate();

        tenant
                .offerRegistrationInvitation("Open-Ended")
                .openEnded();

        assertNotNull(tenant.redefineRegistrationInvitationAs("Open-Ended"));
    }

    @Test
    public void TestOpenEndedInvitationAvailable() {

        Tenant tenant = this.tenantAggregate();

        tenant
                .offerRegistrationInvitation("Open-Ended")
                .openEnded();

        assertTrue(tenant.isRegistrationAvailableThrough("Open-Ended"));
    }

    @Test
    public void TestClosedEndedInvitationAvailable() {

        Tenant tenant = this.tenantAggregate();

        tenant
                .offerRegistrationInvitation("Today-and-Tomorrow")
                .startingOn(this.today())
                .until(this.tomorrow());

        assertTrue(tenant.isRegistrationAvailableThrough("Today-and-Tomorrow"));
    }

    @Test
    public void TestClosedEndedInvitationNotAvailable() {

        Tenant tenant = this.tenantAggregate();

        tenant
                .offerRegistrationInvitation("Tomorrow-and-Day-After-Tomorrow")
                .startingOn(this.tomorrow())
                .until(this.dayAfterTomorrow());

        assertFalse(tenant.isRegistrationAvailableThrough("Tomorrow-and-Day-After-Tomorrow"));
    }

    @Test
    public void TestAvailableInvitationDescriptor() throws Exception {

        Tenant tenant = this.tenantAggregate();

        tenant
                .offerRegistrationInvitation("Open-Ended")
                .openEnded();

        tenant
                .offerRegistrationInvitation("Today-and-Tomorrow")
                .startingOn(this.today())
                .until(this.tomorrow());

        LoggerFactory.getLogger(this.getClass()).info("Tenant Available Registration Invitations {}", tenant.allAvailableRegistrationInvitations().collect(Collectors.toList()));

        assertEquals(2, tenant.allAvailableRegistrationInvitations().count());
    }

    @Test
    public void TestUnavailableInvitationDescriptor() {

        Tenant tenant = this.tenantAggregate();

        tenant
                .offerRegistrationInvitation("Tomorrow-and-Day-After-Tomorrow")
                .startingOn(this.tomorrow())
                .until(this.dayAfterTomorrow());

        assertEquals(1, tenant.allUnavailableRegistrationInvitations().count());
    }

    @Test
    public void TestRegisterUser() {

        Tenant tenant = this.tenantAggregate();

        RegistrationInvitation registrationInvitation =
                this.registrationInvitationEntity(tenant);

        Optional<User> user =
                tenant.registerUser(
                        registrationInvitation.invitationId(),
                        FIXTURE_USERNAME,
                        this.obtainExtendedAccessToken(),
                        new Enablement(true, null, null),
                        this.personEntity(tenant));

        assertTrue(user.isPresent());

        this.userRepository().add(user.get());

        assertNotNull(user.get().enablement());
        assertNotNull(user.get().person());
        assertNotNull(user.get().userDescriptor());
    }
}
