package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.DomainEventPublisher;
import com.neurchi.advisor.common.domain.model.DomainEventSubscriber;
import com.neurchi.advisor.identityaccess.domain.model.IdentityAccessTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest extends IdentityAccessTest {

    private boolean handled;

    @Test
    public void TestUserEnablementEnabled() {

        User user = this.userAggregate();

        assertTrue(user.isEnabled());
    }

    @Test
    public void TestUserEnablementDisabled() {

        final User user = this.userAggregate();

        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<UserEnablementChanged>() {
                    @Override
                    public void handleEvent(final UserEnablementChanged domainEvent) {
                        assertEquals(user.username(), domainEvent.username());
                        handled = true;
                    }

                    @Override
                    public Class<UserEnablementChanged> subscribedToType() {
                        return UserEnablementChanged.class;
                    }
                });

        user.defineEnablement(new Enablement(false, null, null));

        assertFalse(user.isEnabled());
        assertTrue(handled);
    }

    @Test
    public void TestUserEnablementWithinStartEndDates() {

        final User user = this.userAggregate();

        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<UserEnablementChanged>() {
                    @Override
                    public void handleEvent(final UserEnablementChanged domainEvent) {
                        assertEquals(user.username(), domainEvent.username());
                        handled = true;
                    }

                    @Override
                    public Class<UserEnablementChanged> subscribedToType() {
                        return UserEnablementChanged.class;
                    }
                });

        user.defineEnablement(
                new Enablement(
                        true,
                        this.today(),
                        this.tomorrow()));

        assertTrue(user.isEnabled());
        assertTrue(handled);
    }

    @Test
    public void TestUserEnablementOutsideStartEndDates() {

        final User user = this.userAggregate();

        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<UserEnablementChanged>() {
                    @Override
                    public void handleEvent(final UserEnablementChanged domainEvent) {
                        assertEquals(user.username(), domainEvent.username());
                        handled = true;
                    }

                    @Override
                    public Class<UserEnablementChanged> subscribedToType() {
                        return UserEnablementChanged.class;
                    }
                });

        user.defineEnablement(
                new Enablement(
                        true,
                        this.dayBeforeYesterday(),
                        this.yesterday()));

        assertFalse(user.isEnabled());
        assertTrue(handled);
    }

    @Test
    public void TestUserEnablementUnsequencedDates() {

        final User user = this.userAggregate();

        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<UserEnablementChanged>() {
                    @Override
                    public void handleEvent(final UserEnablementChanged domainEvent) {
                        assertEquals(user.username(), domainEvent.username());
                        handled = true;
                    }

                    @Override
                    public Class<UserEnablementChanged> subscribedToType() {
                        return UserEnablementChanged.class;
                    }
                });

        Executable executable = () ->
                user.defineEnablement(
                        new Enablement(
                                true,
                                this.tomorrow(),
                                this.today()));

        assertThrows(IllegalArgumentException.class, executable);
        assertFalse(handled);
    }

    @Test
    public void TestUserDescriptor() {

        User user = this.userAggregate();

        UserDescriptor userDescriptor =
                user.userDescriptor();

        assertNotNull(userDescriptor.emailAddress());
        assertEquals(FIXTURE_USER_EMAIL_ADDRESS, userDescriptor.emailAddress());

        assertNotNull(userDescriptor.tenantId());
        assertEquals(user.tenantId(), userDescriptor.tenantId());

        assertNotNull(userDescriptor.username());
        assertEquals(FIXTURE_USERNAME, userDescriptor.username());
    }

    @Test
    public void TestExtendAccessToken() {

        final User user = this.userAggregate();

        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<UserAccessTokenExtended>() {
                    @Override
                    public void handleEvent(final UserAccessTokenExtended domainEvent) {
                        assertEquals(user.accessToken(), domainEvent.accessToken());
                        assertEquals(user.tenantId(), domainEvent.tenantId());
                        handled = true;
                    }

                    @Override
                    public Class<UserAccessTokenExtended> subscribedToType() {
                        return UserAccessTokenExtended.class;
                    }
                });

        user.extendAccessToken(
                new AccessToken(
                        "new-access-token",
                        "bearer",
                        LocalDateTime.now().plusDays(120)));

        assertTrue(handled);
    }

    @Test
    public void TestExtendAccessTokenFails() {

        final User user = this.userAggregate();

        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<UserAccessTokenExtended>() {
                    @Override
                    public void handleEvent(final UserAccessTokenExtended domainEvent) {
                        assertEquals(user.accessToken(), domainEvent.accessToken());
                        assertEquals(user.tenantId(), domainEvent.tenantId());
                        handled = true;
                    }

                    @Override
                    public Class<UserAccessTokenExtended> subscribedToType() {
                        return UserAccessTokenExtended.class;
                    }
                });

        Executable extendAlreadyExpiredAccessToken =
                () -> user.extendAccessToken(
                        new AccessToken(
                                "new-access-token",
                                "bearer",
                                LocalDateTime.now().minusDays(3)));

        assertThrows(IllegalArgumentException.class, extendAlreadyExpiredAccessToken);

        Executable extendShortTermAccessToken =
                () -> user.extendAccessToken(
                        new AccessToken(
                                "new-access-token",
                                "bearer",
                                LocalDateTime.now().plusDays(60)));

        assertThrows(IllegalArgumentException.class, extendShortTermAccessToken);

        assertFalse(handled);
    }

    @Test
    public void TestUserPersonalContactInformationChanged() {

        final User user = this.userAggregate();

        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<PersonContactInformationChanged>() {
                    @Override
                    public void handleEvent(final PersonContactInformationChanged domainEvent) {
                        assertEquals(user.username(), domainEvent.username());
                        handled = true;
                    }

                    @Override
                    public Class<PersonContactInformationChanged> subscribedToType() {
                        return PersonContactInformationChanged.class;
                    }
                });

        user.changePersonalContactInformation(
                new ContactInformation( // TODO: SmallPicture, LargePicture, etc.
                        new EmailAddress(FIXTURE_USER_EMAIL_ADDRESS2)));

        assertEquals(new EmailAddress(FIXTURE_USER_EMAIL_ADDRESS2), user.person().emailAddress());
        assertTrue(handled);
    }

    @Test
    public void TestUserPersonNameChanged() {

        final User user = this.userAggregate();

        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<PersonNameChanged>() {
                    @Override
                    public void handleEvent(final PersonNameChanged domainEvent) {
                        assertEquals(user.username(), domainEvent.username());
                        assertEquals("Raphaël", domainEvent.name().firstName());
                        assertEquals("Guérault", domainEvent.name().lastName());
                        handled = true;
                    }

                    @Override
                    public Class<PersonNameChanged> subscribedToType() {
                        return PersonNameChanged.class;
                    }
                });

        user.changePersonalName(new FullName("Raphaël", "Guérault"));

        assertTrue(handled);
    }
}
