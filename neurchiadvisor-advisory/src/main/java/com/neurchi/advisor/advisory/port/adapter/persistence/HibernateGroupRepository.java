package com.neurchi.advisor.advisory.port.adapter.persistence;

import com.neurchi.advisor.advisory.domain.model.group.Group;
import com.neurchi.advisor.advisory.domain.model.group.GroupId;
import com.neurchi.advisor.advisory.domain.model.group.GroupRepository;
import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;
import com.neurchi.advisor.common.port.adapter.persistence.hibernate.AbstractHibernateSession;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public class HibernateGroupRepository extends AbstractHibernateSession implements GroupRepository {

    @Override
    public Stream<Group> allGroupsOfTenant(final TenantId tenantId) {
        final Query<Group> query =
                this.session().createQuery(
                        "from Group where tenantId = ?1",
                        Group.class);

        query.setParameter(1, tenantId);

        return query.stream();
    }

    @Override
    public Optional<Group> groupOfSubscriptionInitiationId(final TenantId tenantId, final String subscriptionInitiationId) {
        final Query<Group> query =
                this.session().createQuery(
                        "from Group where tenantId = ?1 and subscriptionInitiationId = ?2",
                        Group.class);

        query.setParameter(1, tenantId);
        query.setParameter(2, subscriptionInitiationId);

        return query.uniqueResultOptional();
    }

    @Override
    public Optional<Group> groupOfId(final TenantId tenantId, final GroupId groupId) {
        final Query<Group> query =
                this.session().createQuery(
                        "from Group where tenantId = ?1 and groupId = ?2",
                        Group.class);

        query.setParameter(1, tenantId);
        query.setParameter(2, groupId);

        return query.uniqueResultOptional();
    }

    @Override
    public void remove(final Group group) {
        this.session().delete(group);
    }

    @Override
    public void add(final Group group) {
        try {
            this.session().save(group);
        } catch (ConstraintViolationException e) {
            throw new IllegalStateException("Group is not unique.", e);
        }
    }
}
