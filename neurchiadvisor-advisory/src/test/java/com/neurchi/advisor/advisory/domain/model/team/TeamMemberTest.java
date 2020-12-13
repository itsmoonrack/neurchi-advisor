package com.neurchi.advisor.advisory.domain.model.team;

import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TeamMemberTest extends TeamCommonTest {

    @Test
    public void TestCreate() {
        TeamMember teamMember =
                new TeamMember(
                        new TenantId("T-12345"),
                        "10157329112631392",
                        "Sylvain Bernard",
                        "Sylvain",
                        "Bernard",
                        "picture.png",
                        "sylvain@neurchiadvisor.com",
                        Instant.now());

        assertNotNull(teamMember);

        this.teamMemberRepository.add(teamMember);

        assertEquals("10157329112631392", teamMember.username());
        assertEquals("Sylvain Bernard", teamMember.name());
        assertEquals("Sylvain", teamMember.firstName());
        assertEquals("Bernard", teamMember.lastName());
        assertEquals("picture.png", teamMember.picture());
        assertEquals("sylvain@neurchiadvisor.com", teamMember.emailAddress());
    }

    @Test
    public void TestChangeEmailAddress() {
        TeamMember teamMember = this.teamMemberForTest();

        assertNotEquals("sylvain.bernard@neurchiadvisor.com", teamMember.emailAddress());

        // later...
        Instant notificationOccurredOn = Instant.now();

        teamMember.changeEmailAddress("sylvain.bernard@neurchiadvisor.com", notificationOccurredOn);

        assertEquals("sylvain.bernard@neurchiadvisor.com", teamMember.emailAddress());
    }

    @Test
    public void TestChangeName() {
        TeamMember teamMember = this.teamMemberForTest();

        assertNotEquals("Wiltord", teamMember.emailAddress());

        // later...
        Instant notificationOccurredOn = Instant.now();

        teamMember.changeName("Sylvain Wiltord", "Sylvain", "Wiltord", notificationOccurredOn);

        assertEquals("Sylvain Wiltord", teamMember.name());
        assertEquals("Sylvain", teamMember.firstName());
        assertEquals("Wiltord", teamMember.lastName());
    }

    @Test
    public void TestDisable() {
        TeamMember teamMember = this.teamMemberForTest();

        assertTrue(teamMember.isEnabled());

        // later...
        Instant notificationOccurredOn = Instant.now();

        teamMember.disable(notificationOccurredOn);

        assertFalse(teamMember.isEnabled());
    }

    @Test
    public void TestEnable() {
        TeamMember teamMember = this.teamMemberForTest();

        teamMember.disable(this.twoHoursEarlierThanNow());

        assertFalse(teamMember.isEnabled());

        // later...
        Instant notificationOccurredOn = Instant.now();

        teamMember.enable(notificationOccurredOn);

        assertTrue(teamMember.isEnabled());
    }

    @Test
    public void TestDisallowEarlierDisabling() {
        TeamMember teamMember = this.teamMemberForTest();

        teamMember.disable(this.twoHoursEarlierThanNow());

        assertFalse(teamMember.isEnabled());

        // later...
        Instant notificationOccurredOn = Instant.now();

        teamMember.enable(notificationOccurredOn);

        assertTrue(teamMember.isEnabled());

        // latent notification...
        teamMember.disable(this.twoMinutesEarlierThanNow());

        assertTrue(teamMember.isEnabled());
    }

    @Test
    public void TestDisallowEarlierEnabling() {
        TeamMember teamMember = this.teamMemberForTest();

        assertTrue(teamMember.isEnabled());

        // later...
        Instant notificationOccurredOn = Instant.now();

        teamMember.disable(notificationOccurredOn);

        assertFalse(teamMember.isEnabled());

        // latent notification...
        teamMember.enable(this.twoMinutesEarlierThanNow());

        assertFalse(teamMember.isEnabled());
    }
}