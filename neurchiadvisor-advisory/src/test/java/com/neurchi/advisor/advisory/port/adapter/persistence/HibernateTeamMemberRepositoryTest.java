package com.neurchi.advisor.advisory.port.adapter.persistence;

import com.neurchi.advisor.advisory.domain.model.team.TeamMember;
import com.neurchi.advisor.advisory.domain.model.team.TeamMemberRepository;
import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class HibernateTeamMemberRepositoryTest {

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Test
    public void TestSave() {
        TeamMember teamMember =
                new TeamMember(
                        new TenantId("12345"),
                        "sbernard",
                        "Sylvain Bernard",
                        "Sylvain",
                        "Bernard",
                        "picture.png",
                        "sylvain@neurchiadvisor.com",
                        Instant.now());

        teamMemberRepository.add(teamMember);

        TeamMember savedTeamMember =
                teamMemberRepository.teamMemberOfIdentity(
                        teamMember.tenantId(),
                        teamMember.username())
                .orElse(null);

        assertNotNull(savedTeamMember);
        assertEquals(teamMember.tenantId(), savedTeamMember.tenantId());
        assertEquals(teamMember.username(), savedTeamMember.username());
        assertEquals(teamMember.name(), savedTeamMember.name());
        assertEquals(teamMember.firstName(), savedTeamMember.firstName());
        assertEquals(teamMember.lastName(), savedTeamMember.lastName());
        assertEquals(teamMember.picture(), savedTeamMember.picture());
        assertEquals(teamMember.emailAddress(), savedTeamMember.emailAddress());

        Stream<TeamMember> savedTeamMembers =
                teamMemberRepository.allTeamMembersOfTenant(teamMember.tenantId());

        assertNotNull(savedTeamMembers);
        assertEquals(1, savedTeamMembers.count());
    }

    @Test
    public void TestRemove() {
        TeamMember teamMember1 =
                new TeamMember(
                        new TenantId("12345"),
                        "sbernard",
                        "Sylvain Bernard",
                        "Sylvain",
                        "Bernard",
                        "picture.png",
                        "sylvain@neurchiadvisor.com",
                        Instant.now());

        TeamMember teamMember2 =
                new TeamMember(
                        new TenantId("12345"),
                        "rgerault",
                        "Raphaël Guérault",
                        "Raphaël",
                        "Guérault",
                        "picture.png",
                        "raphael@neurchiadvisor.com",
                        Instant.now());

        teamMemberRepository.add(teamMember1);
        teamMemberRepository.add(teamMember2);

        teamMemberRepository.remove(teamMember1);

        TenantId tenantId = teamMember2.tenantId();

        Collection<TeamMember> savedTeamMembers = teamMemberRepository.allTeamMembersOfTenant(tenantId).collect(Collectors.toList());
        assertFalse(savedTeamMembers.isEmpty());
        assertEquals(1, savedTeamMembers.size());
        assertEquals(teamMember2.username(), savedTeamMembers.iterator().next().username());

        teamMemberRepository.remove(teamMember2);

        savedTeamMembers = teamMemberRepository.allTeamMembersOfTenant(tenantId).collect(Collectors.toList());
        assertTrue(savedTeamMembers.isEmpty());
    }
}
