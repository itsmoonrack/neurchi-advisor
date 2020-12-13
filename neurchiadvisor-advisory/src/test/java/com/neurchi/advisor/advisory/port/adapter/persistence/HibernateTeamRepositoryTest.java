package com.neurchi.advisor.advisory.port.adapter.persistence;

import com.neurchi.advisor.advisory.domain.model.team.Team;
import com.neurchi.advisor.advisory.domain.model.team.TeamRepository;
import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class HibernateTeamRepositoryTest {

    @Autowired
    private TeamRepository teamRepository;

    @Test
    public void TestSave() {
        Team team = new Team(new TenantId("12345"), "team1");

        teamRepository.add(team);

        Team savedTeam = teamRepository.teamNamed(team.tenantId(), team.name());

        assertNotNull(savedTeam);
        assertEquals(team.tenantId(), savedTeam.tenantId());
        assertEquals(team.name(), savedTeam.name());

        Stream<Team> savedTeams = teamRepository.allTeamsOfTenant(team.tenantId());

        assertNotNull(savedTeams);
        assertEquals(1, savedTeams.count());
    }

    @Test
    public void TestRemove() {
        Team team1 = new Team(new TenantId("12345"), "team1");

        Team team2 = new Team(new TenantId("12345"), "team2");

        teamRepository.add(team1);
        teamRepository.add(team2);

        teamRepository.remove(team1);

        TenantId tenantId = team2.tenantId();

        Collection<Team> savedTeams = teamRepository.allTeamsOfTenant(tenantId).collect(Collectors.toList());
        assertFalse(savedTeams.isEmpty());
        assertEquals(1, savedTeams.size());
        assertEquals(team2.name(), savedTeams.iterator().next().name());

        teamRepository.remove(team2);

        savedTeams = teamRepository.allTeamsOfTenant(tenantId).collect(Collectors.toList());
        assertTrue(savedTeams.isEmpty());
    }
}