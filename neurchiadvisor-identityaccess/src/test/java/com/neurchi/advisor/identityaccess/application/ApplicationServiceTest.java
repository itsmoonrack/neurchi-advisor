package com.neurchi.advisor.identityaccess.application;

import com.neurchi.advisor.common.domain.model.DomainEventPublisher;
import com.neurchi.advisor.common.event.EventStore;
import com.neurchi.advisor.common.persistence.CleanableStore;
import com.neurchi.advisor.identityaccess.domain.model.access.AuthorizationService;
import com.neurchi.advisor.identityaccess.domain.model.access.Role;
import com.neurchi.advisor.identityaccess.domain.model.access.RoleRepository;
import com.neurchi.advisor.identityaccess.domain.model.identity.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(locations = {
        "classpath:application-context-common.xml",
        "classpath:application-context-identityaccess-application.xml",
        "classpath:application-context-identityaccess-test.xml"})
public abstract class ApplicationServiceTest {

    protected static final String FIXTURE_GROUP_NAME = "Test Group";
    protected static final String FIXTURE_ROLE_NAME = "Test Role";
    protected static final String FIXTURE_TENANT_DESCRIPTION = "This is a test tenant.";
    protected static final String FIXTURE_TENANT_NAME = "Test Tenant";
    protected static final String FIXTURE_USER_EMAIL_ADDRESS = "jdoe@saasovation.com";
    protected static final String FIXTURE_USER_EMAIL_ADDRESS2 = "zdoe@saasovation.com";
    protected static final String FIXTURE_USERNAME = "jdoe";
    protected static final String FIXTURE_ACCESS_TOKEN = "access-token";
    protected static final String FIXTURE_TOKEN_TYPE = "bearer";
    protected static final LocalDateTime FIXTURE_EXPIRES_IN = LocalDateTime.now().plusDays(90);
    protected static final String FIXTURE_USERNAME2 = "zdoe";

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    protected EventStore eventStore;

    private Tenant activeTenant;

    protected Group group1Aggregate() {
        return this.tenantAggregate()
                .provisionGroup(FIXTURE_GROUP_NAME + " 1", "A test group 1.");
    }

    protected Group group2Aggregate() {
        return this.tenantAggregate()
                .provisionGroup(FIXTURE_GROUP_NAME + " 2", "A test group 2.");
    }

    protected Role roleAggregate() {
        return this.tenantAggregate()
                .provisionRole(FIXTURE_ROLE_NAME, "A test role.", true);
    }

    protected Tenant tenantAggregate() {
        if (activeTenant == null) {

            activeTenant =
                    this
                            .tenantProvisioningService()
                            .provisionTenant(
                                    FIXTURE_TENANT_NAME,
                                    FIXTURE_TENANT_DESCRIPTION,
                                    "sbernard",
                                    new AccessToken(FIXTURE_ACCESS_TOKEN, FIXTURE_TOKEN_TYPE, FIXTURE_EXPIRES_IN),
                                    new FullName("Sylvain", "Bernard"),
                                    new EmailAddress(FIXTURE_USER_EMAIL_ADDRESS));
        }

        return activeTenant;
    }

    protected User userAggregate() {

        Tenant tenant = this.tenantAggregate();

        RegistrationInvitation invitation =
                tenant.offerRegistrationInvitation("open-ended").openEnded();

        User user =
                tenant.registerUser(
                        invitation.invitationId(),
                        "nberjaud",
                        new AccessToken(FIXTURE_ACCESS_TOKEN, FIXTURE_TOKEN_TYPE, FIXTURE_EXPIRES_IN),
                        Enablement.indefiniteEnablement(),
                        new Person(
                                tenant.tenantId(),
                                new FullName("Noélie", "Berjaud"),
                                new ContactInformation(
                                        new EmailAddress(FIXTURE_USER_EMAIL_ADDRESS))))
                        .orElseThrow();

        return user;
    }

    @BeforeEach
    protected void setUp() {

        DomainEventPublisher.instance().reset();

        this.clean((CleanableStore) this.eventStore);
        this.clean((CleanableStore) groupRepository());
        this.clean((CleanableStore) roleRepository());
        this.clean((CleanableStore) tenantRepository());
        this.clean((CleanableStore) userRepository());
    }

    @AfterEach
    protected void tearDown() {
        this.clean((CleanableStore) this.eventStore);
        this.clean((CleanableStore) groupRepository());
        this.clean((CleanableStore) roleRepository());
        this.clean((CleanableStore) tenantRepository());
        this.clean((CleanableStore) userRepository());
    }

    private void clean(final CleanableStore cleanableStore) {
        cleanableStore.clean();
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
        return applicationContext.getBean(GroupMemberService.class);
    }

    protected TenantProvisioningService tenantProvisioningService() {
        return applicationContext.getBean(TenantProvisioningService.class);
    }

    protected AccessApplicationService accessApplicationService() {
        return applicationContext.getBean(AccessApplicationService.class);
    }

    protected IdentityApplicationService identityApplicationService() {
        return applicationContext.getBean(IdentityApplicationService.class);
    }

    protected NotificationApplicationService notificationApplicationService() {
        return applicationContext.getBean(NotificationApplicationService.class);
    }
}
