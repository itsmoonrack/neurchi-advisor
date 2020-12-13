package com.neurchi.advisor.advisory.domain.model.team;

import com.neurchi.advisor.advisory.domain.model.DomainTest;
import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.*;

class TeamCommonTest extends DomainTest {

    protected Instant twoHoursEarlierThanNow() {
        return Instant.now().minus(2, HOURS);
    }

    protected Instant twoMinutesEarlierThanNow() {
        return Instant.now().minus(2, MINUTES);
    }

    protected GroupOwner groupOwnerForTest() {
        return this.groupOwnerForTest1();
    }

    protected GroupOwner groupOwnerForTest1() {
        return new GroupOwner(
                new TenantId("T-12345"),
                "10157329112631392",
                "Sylvain Bernard",
                "Sylvain",
                "Bernard",
                "picture.png",
                "sylvain@neurchiadvisor.com",
                Instant.now().minus(30, DAYS));
    }

    protected Team teamForTest() {
        return new Team(
                new TenantId("T-12345"),
                "Identity and Access Management");
    }

    protected TeamMember teamMemberForTest() {
        return this.teamMemberForTest1();
    }

    protected TeamMember teamMemberForTest1() {
        return new TeamMember(
                new TenantId("T-12345"),
                "10157329112631392",
                "Sylvain Bernard",
                "Sylvain",
                "Bernard",
                "picture.png",
                "sylvain@neurchiadvisor.com",
                Instant.now().minus(30, DAYS));
    }

    protected TeamMember teamMemberForTest2() {
        return new TeamMember(
                new TenantId("T-12345"),
                "facebook-id-182412439",
                "Valentina Conti",
                "Valentina",
                "Conti",
                "picture.png",
                "valentina@neurchiadvisor.com",
                Instant.now().minus(30, DAYS));
    }

    protected TeamMember teamMemberForTest3() {
        return new TeamMember(
                new TenantId("T-12345"),
                "facebook-id-456489123",
                "Noélie Berjaud",
                "Noélie",
                "Berjaud",
                "picture.png",
                "noelie@neurchiadvisor.com",
                Instant.now().minus(30, DAYS));
    }
}
