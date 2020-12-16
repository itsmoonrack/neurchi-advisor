package com.neurchi.advisor.identityaccess.resource;

import com.neurchi.advisor.common.domain.model.DomainEventPublisher;
import com.neurchi.advisor.common.notification.NotificationLog;
import com.neurchi.advisor.common.notification.NotificationLogReader;
import com.neurchi.advisor.common.notification.NotificationReader;
import com.neurchi.advisor.identityaccess.application.command.ChangeContactInfoCommand;
import com.neurchi.advisor.identityaccess.application.command.ProvisionTenantCommand;
import com.neurchi.advisor.identityaccess.application.command.RegisterUserCommand;
import com.neurchi.advisor.identityaccess.domain.model.identity.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationResourceTest extends ResourceTest {

    @Test
    public void TestBasicNotificationLog() throws Exception {
        this.generateUserEvents();

        NotificationLog currentNotificationLog =
                this
                        .notificationApplicationService()
                        .currentNotificationLog();

        assertNotNull(currentNotificationLog);

        URI uri = new URI("http://localhost:" + this.port() + "/notifications");

        ResponseEntity<String> response = this.restTemplate().getForEntity(uri, String.class);

        NotificationLogReader log =
                new NotificationLogReader(
                        response.getBody(),
                        response.getHeaders().getOrEmpty(HttpHeaders.LINK));

        assertFalse(log.isArchived());
        assertNotNull(log.id());

        for (NotificationReader notification : log) {

            String typeName = notification.typeName();

            assertTrue(typeName.endsWith("UserRegistered")
                    || typeName.endsWith("PersonNameChanged")
                    || typeName.endsWith("UserAccessTokenExtended"));
        }
    }

    @Test
    public void TestPersonContactInformationChangedNotification() throws Exception {
        this.generateUserEvents();

        this
                .identityApplicationService()
                .changeUserContactInformation(
                        new ChangeContactInfoCommand(
                                this.tenantAggregate().tenantId().id(),
                                FIXTURE_USERNAME + 0,
                                FIXTURE_USER_EMAIL_ADDRESS2));

        URI uri = new URI("http://localhost:" + this.port() + "/notifications");

        ResponseEntity<String> response = this.restTemplate().getForEntity(uri, String.class);

        NotificationLogReader log =
                new NotificationLogReader(
                        response.getBody(),
                        response.getHeaders().getOrEmpty(HttpHeaders.LINK));

        assertFalse(log.isArchived());
        assertNotNull(log.id());

        boolean found = false;

        for (NotificationReader notification : log) {
            String typeName = notification.typeName();

            if (typeName.endsWith("PersonContactInformationChanged")) {
                String emailAddress =
                        notification.eventStringValue(
                                "contactInformation.emailAddress.address");

                assertEquals(
                        FIXTURE_USER_EMAIL_ADDRESS2,
                        emailAddress);

                found = true;
            }
        }

        assertTrue(found);
    }

    @Test
    public void TestTenantProvisionedNotification() {
        Tenant newTenant =
                this
                    .identityApplicationService()
                    .provisionTenant(
                            new ProvisionTenantCommand(
                                    "298448690577160",
                                    "Neurchi de Finances: The Bottom Fishing Club",
                                    "10157329112631392",
                                    "Sylvain",
                                    "Bernard",
                                    "access-token",
                                    "bearer",
                                    LocalDateTime.now().plusDays(90),
                                    "sylvain@neurchiadvisor.com"));

        assertNotNull(newTenant);


    }

    private void generateUserEvents() {
        Tenant tenant = this.tenantAggregate();
        Person person = this.userAggregate().person();

        String invitationId =
                tenant
                        .allAvailableRegistrationInvitations()
                        .findAny()
                        .map(InvitationDescriptor::invitationId)
                        .orElse(null);

        for (int index = 0; index < 25; ++index) {

            User user =
                    this
                            .identityApplicationService()
                            .registerUser(
                                    new RegisterUserCommand(
                                            tenant.tenantId().id(),
                                            invitationId,
                                            FIXTURE_USERNAME + index,
                                            FIXTURE_ACCESS_TOKEN,
                                            FIXTURE_TOKEN_TYPE,
                                            FIXTURE_EXPIRES_IN,
                                            "Sylvain",
                                            "Bernard",
                                            true,
                                            null,
                                            null,
                                            person.contactInformation().emailAddress().address()));

            if ((index % 2) == 0) {
                PersonNameChanged event =
                        newPersonNameChanged(
                                user.tenantId(),
                                user.username(),
                                user.person().name());

                this.eventStore.append(event);
            }

            if ((index % 3) == 0) {
                UserAccessTokenExtended event =
                        newUserAccessTokenExtended(
                                user.tenantId(),
                                user.username(),
                                user.accessToken());

                this.eventStore.append(event);
            }
        }

        DomainEventPublisher.instance().reset();
    }

    private PersonNameChanged newPersonNameChanged(final TenantId tenantId, final String username, final FullName name) {
        try {
            Constructor<PersonNameChanged> constructor = PersonNameChanged.class.getDeclaredConstructor(TenantId.class, String.class, FullName.class);
            constructor.setAccessible(true);
            return constructor.newInstance(tenantId, username, name);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    private UserAccessTokenExtended newUserAccessTokenExtended(final TenantId tenantId, final String username, final AccessToken accessToken) {
        try {
            Constructor<UserAccessTokenExtended> constructor = UserAccessTokenExtended.class.getDeclaredConstructor(TenantId.class, String.class, AccessToken.class);
            constructor.setAccessible(true);
            return constructor.newInstance(tenantId, username, accessToken);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }
}
