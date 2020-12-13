package com.neurchi.advisor.advisory.domain.model.team;

import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TeamTest extends TeamCommonTest {

    @Test
    public void TestCreate() {
        TenantId tenantId = new TenantId("T-12345");

        Team team = new Team(tenantId, "Identity and Access Management");

        this.teamRepository.add(team);

        assertEquals("Identity and Access Management", team.name());
    }

    @Test
    public void TestAssignGroupOwner() {
        Team team = this.teamForTest();

        GroupOwner groupOwner = this.groupOwnerForTest();

        team.assignGroupOwner(groupOwner);

        assertNotNull(team.groupOwner());
        assertEquals(groupOwner.groupOwnerId(), team.groupOwner().groupOwnerId());
    }

    @Test
    public void TestAssignTeamMembers() {
        Team team = this.teamForTest();

        TeamMember teamMember1 = this.teamMemberForTest1();
        TeamMember teamMember2 = this.teamMemberForTest2();
        TeamMember teamMember3 = this.teamMemberForTest3();

        team.assignTeamMember(teamMember1);
        team.assignTeamMember(teamMember2);
        team.assignTeamMember(teamMember3);

        assertFalse(team.allTeamMembers().isEmpty());
        assertEquals(3, team.allTeamMembers().size());

        assertTrue(team.isTeamMember(teamMember1));
        assertTrue(team.isTeamMember(teamMember2));
        assertTrue(team.isTeamMember(teamMember3));
    }

    @Test
    public void TestRemoveTeamMembers() {
        Team team = this.teamForTest();

        TeamMember teamMember1 = this.teamMemberForTest1();
        TeamMember teamMember2 = this.teamMemberForTest2();
        TeamMember teamMember3 = this.teamMemberForTest3();

        team.assignTeamMember(teamMember1);
        team.assignTeamMember(teamMember2);
        team.assignTeamMember(teamMember3);

        assertFalse(team.allTeamMembers().isEmpty());
        assertEquals(3, team.allTeamMembers().size());

        team.removeTeamMember(teamMember2);

        assertTrue(team.isTeamMember(teamMember1));
        assertFalse(team.isTeamMember(teamMember2));
        assertTrue(team.isTeamMember(teamMember3));
    }
}
