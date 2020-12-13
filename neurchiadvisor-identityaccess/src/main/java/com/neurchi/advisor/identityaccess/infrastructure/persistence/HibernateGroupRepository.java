package com.neurchi.advisor.identityaccess.infrastructure.persistence;

import com.neurchi.advisor.common.port.adapter.persistence.hibernate.AbstractHibernateSession;
import com.neurchi.advisor.identityaccess.domain.model.identity.Group;
import com.neurchi.advisor.identityaccess.domain.model.identity.GroupRepository;
import com.neurchi.advisor.identityaccess.domain.model.identity.TenantId;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public class HibernateGroupRepository extends AbstractHibernateSession implements GroupRepository {

    @Override
    public void add(final Group group) {
        try {
            this.session().save(group);
        } catch (ConstraintViolationException e) {
            throw new IllegalStateException("Group is not unique.", e);
        }
    }

    @Override
    public Stream<Group> allGroups(final TenantId tenantId) {
        Query<Group> query = this.session()
                .createQuery("from Group where tenantId = ?1 and name not like ?2", Group.class);

        query.setParameter(1, tenantId);
        query.setParameter(2, Group.ROLE_GROUP_PREFIX + '%');

        return query.stream();
    }

    @Override
    public Optional<Group> groupNamed(final TenantId tenantId, final String groupName) {
        if (groupName.startsWith(Group.ROLE_GROUP_PREFIX)) {
            throw new IllegalArgumentException("May not find internal group.");
        }

        Query<Group> query = this.session()
                .createQuery("from Group where tenantId = ?1 and name = ?2", Group.class);

        query.setParameter(1, tenantId);
        query.setParameter(2, groupName);

        return query.uniqueResultOptional();
    }

    @Override
    public void remove(final Group group) {
        this.session().delete(group);
    }
}
