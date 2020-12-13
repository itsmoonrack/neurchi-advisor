package com.neurchi.advisor.advisory.domain.model.team;

import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class GroupOwnerTest extends TeamCommonTest {

    @Test
    public void TestCreate() {
        GroupOwner groupOwner =
                new GroupOwner(
                        new TenantId("T-12345"),
                        "10157329112631392",
                        "Sylvain Bernard",
                        "Sylvain",
                        "Bernard",
                        "picture.png",
                        "sylvain@neurchiadvisor.com",
                        Instant.now());

        assertNotNull(groupOwner);

        this.groupOwnerRepository.add(groupOwner);

        assertEquals("10157329112631392", groupOwner.username());
        assertEquals("Sylvain Bernard", groupOwner.name());
        assertEquals("Sylvain", groupOwner.firstName());
        assertEquals("Bernard", groupOwner.lastName());
        assertEquals("picture.png", groupOwner.picture());
        assertEquals("sylvain@neurchiadvisor.com", groupOwner.emailAddress());
    }

    @Test
    public void TestChangeEmailAddress() {
        GroupOwner groupOwner = this.groupOwnerForTest();

        assertNotEquals("sylvain.bernard@neurchiadvisor.com", groupOwner.emailAddress());

        // later...
        Instant notificationOccurredOn = Instant.now();

        groupOwner.changeEmailAddress("sylvain.bernard@neurchiadvisor.com", notificationOccurredOn);

        assertEquals("sylvain.bernard@neurchiadvisor.com", groupOwner.emailAddress());
    }

    @Test
    public void TestChangeName() {
        GroupOwner groupOwner = this.groupOwnerForTest();

        assertNotEquals("Wiltord", groupOwner.emailAddress());

        // later...
        Instant notificationOccurredOn = Instant.now();

        groupOwner.changeName("Sylvain Wiltord", "Sylvain", "Wiltord", notificationOccurredOn);

        assertEquals("Sylvain Wiltord", groupOwner.name());
        assertEquals("Sylvain", groupOwner.firstName());
        assertEquals("Wiltord", groupOwner.lastName());
    }

    @Test
    public void TestDisable() {
        GroupOwner groupOwner = this.groupOwnerForTest();

        assertTrue(groupOwner.isEnabled());

        // later...
        Instant notificationOccurredOn = Instant.now();

        groupOwner.disable(notificationOccurredOn);

        assertFalse(groupOwner.isEnabled());
    }

    @Test
    public void TestEnable() {
        GroupOwner groupOwner = this.groupOwnerForTest();

        groupOwner.disable(this.twoHoursEarlierThanNow());

        assertFalse(groupOwner.isEnabled());

        // later...
        Instant notificationOccurredOn = Instant.now();

        groupOwner.enable(notificationOccurredOn);

        assertTrue(groupOwner.isEnabled());
    }

    @Test
    public void TestDisallowEarlierDisabling() {
        GroupOwner groupOwner = this.groupOwnerForTest();

        groupOwner.disable(this.twoHoursEarlierThanNow());

        assertFalse(groupOwner.isEnabled());

        // later...
        Instant notificationOccurredOn = Instant.now();

        groupOwner.enable(notificationOccurredOn);

        assertTrue(groupOwner.isEnabled());

        // latent notification...
        groupOwner.disable(this.twoMinutesEarlierThanNow());

        assertTrue(groupOwner.isEnabled());
    }

    @Test
    public void TestDisallowEarlierEnabling() {
        GroupOwner groupOwner = this.groupOwnerForTest();

        assertTrue(groupOwner.isEnabled());

        // later...
        Instant notificationOccurredOn = Instant.now();

        groupOwner.disable(notificationOccurredOn);

        assertFalse(groupOwner.isEnabled());

        // latent notification...
        groupOwner.enable(this.twoMinutesEarlierThanNow());

        assertFalse(groupOwner.isEnabled());
    }
}