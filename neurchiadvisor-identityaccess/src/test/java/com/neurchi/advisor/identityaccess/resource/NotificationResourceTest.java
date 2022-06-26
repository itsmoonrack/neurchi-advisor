package com.neurchi.advisor.identityaccess.resource;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.neurchi.advisor.common.domain.model.DomainEventPublisher;
import com.neurchi.advisor.common.notification.NotificationLog;
import com.neurchi.advisor.common.notification.NotificationLogFactory;
import com.neurchi.advisor.common.notification.NotificationLogReader;
import com.neurchi.advisor.common.notification.NotificationReader;
import com.neurchi.advisor.identityaccess.application.command.ChangeContactInfoCommand;
import com.neurchi.advisor.identityaccess.application.command.ProvisionTenantCommand;
import com.neurchi.advisor.identityaccess.application.command.RegisterUserCommand;
import com.neurchi.advisor.identityaccess.domain.model.identity.*;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationResourceTest extends ResourceTest {

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
    public void TestTenantProvisionedNotification() throws Exception {
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

            if (typeName.endsWith("TenantProvisioned")) {
                String tenantId = notification.eventStringValue("tenantId.id");

                assertEquals(newTenant.tenantId().id(), tenantId);

                found = true;
            }
        }

        assertTrue(found);
    }

    @Test
    public void TestNotificationNavigation() throws Exception {
        this.generateUserEvents();

        URI uri = new URI("http://localhost:" + this.port() + "/notifications");

        ResponseEntity<String> response = this.restTemplate().getForEntity(uri, String.class);

        NotificationLogReader log =
                new NotificationLogReader(
                        response.getBody(),
                        response.getHeaders().getOrEmpty(HttpHeaders.LINK));

        assertFalse(log.isArchived());
        assertNotNull(log.id());
        assertFalse(log.hasNext());
        assertTrue(log.hasSelf());
        assertTrue(log.hasPrevious());

        int count = 0;

        while (log.hasPrevious()) {
            ++count;

            Link previous = log.previous();

            response = this.restTemplate().getForEntity(previous.toUri(), String.class);

            log = new NotificationLogReader(
                    response.getBody(),
                    response.getHeaders().getOrEmpty(HttpHeaders.LINK));

            assertTrue(log.isArchived());
            assertNotNull(log.id());
            assertTrue(log.hasNext());
            assertTrue(log.hasSelf());
        }

        assertTrue(count >= 1);
    }

    @Test
    public void TestXMLRepresentation() throws Exception {

        this.generateUserEvents();

        SimpleModule module = new SimpleModule("NotificationSerializer", new Version(1, 0, 0, "alpha"));
//        module.addSerializer(new NotificationSerializer());

        ObjectMapper objectMapper = new XmlMapper();
        objectMapper.registerModule(module);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .deactivateDefaultTyping()
                .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
                .registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module());

        NotificationLogFactory factory = new NotificationLogFactory(this.eventStore);

        NotificationLog log = factory.createCurrentNotificationLog();

        String serializedLog = objectMapper.writeValueAsString(log);

        System.out.println(serializedLog);
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
                UserEnablementChanged event =
                        newUserEnablementChanged(
                                user.tenantId(),
                                user.username(),
                                user.enablement().isEnabled());

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

    private UserEnablementChanged newUserEnablementChanged(final TenantId tenantId, final String username, final boolean enabled) {
        try {
            Constructor<UserEnablementChanged> constructor = UserEnablementChanged.class.getDeclaredConstructor(TenantId.class, String.class, Enablement.class);
            constructor.setAccessible(true);
            return constructor.newInstance(tenantId, username, new Enablement(enabled, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)));
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }
}
