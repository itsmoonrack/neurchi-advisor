package com.neurchi.advisor.advisory.port.adapter.persistence;

import com.neurchi.advisor.advisory.domain.model.team.GroupOwner;
import com.neurchi.advisor.advisory.domain.model.team.GroupOwnerRepository;
import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;
import com.neurchi.advisor.common.port.adapter.persistence.hibernate.AbstractHibernateSession;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public class HibernateGroupOwnerRepository extends AbstractHibernateSession implements GroupOwnerRepository {

    @Override
    public Stream<GroupOwner> allGroupOwnersOfTenant(final TenantId tenantId) {
        final Query<GroupOwner> query = this.session()
                .createQuery(
                        "from GroupOwner where tenantId = ?1",
                        GroupOwner.class);

        query.setParameter(1, tenantId);

        return query.stream();
    }

    @Override
    public void remove(final GroupOwner groupOwner) {
        this.session().delete(groupOwner);
    }

    @Override
    public void add(final GroupOwner groupOwner) {
        try {
            this.session().save(groupOwner);
        } catch (ConstraintViolationException e) {
            throw new IllegalStateException("Group owner is not unique.", e);
        }
    }

    @Override
    public Optional<GroupOwner> groupOwnerOfIdentity(final TenantId tenantId, final String username) {
        final Query<GroupOwner> query = this.session()
                .createQuery(
                        "from GroupOwner where tenantId = ?1 and username = ?2",
                        GroupOwner.class);

        query.setParameter(1, tenantId);
        query.setParameter(2, username);

        return query.uniqueResultOptional();
    }
}
