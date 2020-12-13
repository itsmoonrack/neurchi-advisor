package com.neurchi.advisor.advisory.port.adapter.persistence;

import com.neurchi.advisor.advisory.domain.model.team.TeamMember;
import com.neurchi.advisor.advisory.domain.model.team.TeamMemberRepository;
import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;
import com.neurchi.advisor.common.port.adapter.persistence.hibernate.AbstractHibernateSession;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public class HibernateTeamMemberRepository extends AbstractHibernateSession implements TeamMemberRepository {

    @Override
    public Stream<TeamMember> allTeamMembersOfTenant(final TenantId tenantId) {
        final Query<TeamMember> query = this.session()
                .createQuery(
                        "from TeamMember where tenantId = ?1",
                        TeamMember.class);

        query.setParameter(1, tenantId);

        return query.stream();
    }

    @Override
    public void remove(final TeamMember teamMember) {
        this.session().delete(teamMember);
    }

    @Override
    public void add(final TeamMember teamMember) {
        try {
            this.session().save(teamMember);
        } catch (ConstraintViolationException e) {
            throw new IllegalStateException("Team member is not unique.", e);
        }
    }

    @Override
    public Optional<TeamMember> teamMemberOfIdentity(final TenantId tenantId, final String username) {
        final Query<TeamMember> query = this.session()
                .createQuery(
                        "from TeamMember where tenantId = ?1 and username = ?2",
                        TeamMember.class);

        query.setParameter(1, tenantId);
        query.setParameter(2, username);

        return query.uniqueResultOptional();
    }
}
