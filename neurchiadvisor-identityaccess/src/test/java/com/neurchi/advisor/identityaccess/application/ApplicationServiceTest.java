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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
    protected static final String FIXTURE_CODE_PARAMETER = "core-parameter";

    @Autowired
    protected EventStore eventStore;
    @Autowired
    protected GroupRepository groupRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected TenantRepository tenantRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected GroupMemberService groupMemberService;
    @Autowired
    protected TenantProvisioningService tenantProvisioningService;
    @Autowired
    protected AccessApplicationService accessApplicationService;
    @Autowired
    protected IdentityApplicationService identityApplicationService;
    @Autowired
    protected NotificationApplicationService notificationApplicationService;

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
                        FIXTURE_USERNAME,
                        FIXTURE_CODE_PARAMETER)
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
        return userRepository;
    }

    protected TenantRepository tenantRepository() {
        return tenantRepository;
    }

    protected GroupRepository groupRepository() {
        return groupRepository;
    }

    protected RoleRepository roleRepository() {
        return roleRepository;
    }

    protected GroupMemberService groupMemberService() {
        return groupMemberService;
    }

    protected TenantProvisioningService tenantProvisioningService() {
        return tenantProvisioningService;
    }

    protected AccessApplicationService accessApplicationService() {
        return accessApplicationService;
    }

    protected IdentityApplicationService identityApplicationService() {
        return identityApplicationService;
    }

    protected NotificationApplicationService notificationApplicationService() {
        return notificationApplicationService;
    }
}
