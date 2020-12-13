package com.neurchi.advisor.advisory.domain.model.team;

import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;
import com.neurchi.advisor.common.domain.model.ConcurrencySafeEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Team extends ConcurrencySafeEntity {

    private String name;
    private GroupOwner groupOwner;
    private Set<TeamMember> teamMembers;
    private TenantId tenantId;

    public Team(final TenantId tenantId, final String name, final GroupOwner groupOwner) {
        this();

        this.setName(name);
        this.setGroupOwner(groupOwner);
        this.setTenantId(tenantId);
    }

    public Team(final TenantId tenantId, final String name) {
        this();

        this.setName(name);
        this.setTenantId(tenantId);
    }

    public Set<TeamMember> allTeamMembers() {
        return Collections.unmodifiableSet(this.teamMembers());
    }

    public void assignGroupOwner(final GroupOwner groupOwner) {
        this.assertArgumentEquals(this.tenantId(), groupOwner.tenantId(), "Team member must be of the same tenant.");

        this.setGroupOwner(groupOwner);
    }

    public void assignTeamMember(final TeamMember teamMember) {
        this.assertArgumentEquals(this.tenantId(), teamMember.tenantId(), "Team member must be of the same tenant.");

        this.teamMembers().add(teamMember);
    }

    public String name() {
        return this.name;
    }

    public boolean isTeamMember(final TeamMember teamMember) {
        this.assertArgumentEquals(this.tenantId(), teamMember.tenantId(), "Team member must be of the same tenant.");

        return this.teamMembers().contains(teamMember);
    }

    public GroupOwner groupOwner() {
        return this.groupOwner;
    }

    public void removeTeamMember(final TeamMember teamMember) {
        this.assertArgumentEquals(this.tenantId(), teamMember.tenantId(), "Team member must be of the same tenant.");

        this.teamMembers().remove(teamMember);
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Team team = (Team) o;
        return name.equals(team.name) &&
                tenantId.equals(team.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tenantId);
    }

    protected Team() {
        // Needed by Hibernate.
        this.setTeamMembers(new HashSet<>(0));
    }

    private void setName(String aName) {
        this.assertArgumentNotEmpty(aName, "The name must be provided.");
        this.assertArgumentLength(aName, 100, "The name must be 100 characters or less.");

        this.name = aName;
    }

    private void setGroupOwner(final GroupOwner groupOwner) {
        this.assertArgumentNotNull(groupOwner, "The group owner must be provided.");
        this.assertArgumentEquals(this.tenantId(), groupOwner.tenantId(), "The group owner must be of the same tenant.");

        this.groupOwner = groupOwner;
    }

    private Set<TeamMember> teamMembers() {
        return this.teamMembers;
    }

    private void setTeamMembers(final Set<TeamMember> teamMembers) {
        this.teamMembers = teamMembers;
    }

    private void setTenantId(final TenantId tenantId) {
        this.assertArgumentNotNull(tenantId, "The tenantId must be provided.");

        this.tenantId = tenantId;
    }
}
