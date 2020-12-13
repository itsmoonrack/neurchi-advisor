package com.neurchi.advisor.advisory.port.adapter.persistence;

import com.neurchi.advisor.advisory.domain.model.team.Team;
import com.neurchi.advisor.advisory.domain.model.team.TeamRepository;
import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;
import com.neurchi.advisor.common.port.adapter.persistence.hibernate.AbstractHibernateSession;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public class HibernateTeamRepository extends AbstractHibernateSession implements TeamRepository {

    @Override
    public Stream<Team> allTeamsOfTenant(final TenantId tenantId) {
        final Query<Team> query = this.session()
                .createQuery(
                        "from Team where tenantId = ?1",
                        Team.class);

        query.setParameter(1, tenantId);

        return query.stream();
    }

    @Override
    public void remove(final Team team) {
        this.session().delete(team);
    }

    @Override
    public void add(final Team team) {
        try {
            this.session().save(team);
        } catch (ConstraintViolationException e) {
            throw new IllegalStateException("Team is not unique.", e);
        }
    }

    @Override
    public Team teamNamed(final TenantId tenantId, final String name) {
        final Query<Team> query = this.session()
                .createQuery(
                        "from Team where tenantId = ?1 and name = ?2",
                        Team.class);

        query.setParameter(1, tenantId);
        query.setParameter(2, name);

        return query.uniqueResult();
    }
}
