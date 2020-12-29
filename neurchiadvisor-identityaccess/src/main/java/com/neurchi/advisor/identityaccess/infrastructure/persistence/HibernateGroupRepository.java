package com.neurchi.advisor.identityaccess.infrastructure.persistence;

import com.neurchi.advisor.common.port.adapter.persistence.hibernate.AbstractHibernateSession;
import com.neurchi.advisor.identityaccess.domain.model.identity.Group;
import com.neurchi.advisor.identityaccess.domain.model.identity.GroupMemberType;
import com.neurchi.advisor.identityaccess.domain.model.identity.GroupRepository;
import com.neurchi.advisor.identityaccess.domain.model.identity.TenantId;
import org.hibernate.HibernateException;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.query.Query;
import org.hibernate.usertype.UserCollectionType;
import org.springframework.stereotype.Repository;

import java.util.*;
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

        // ensures for each operation on groupMembers() are filtered to Group only.
        this.session().enableFilter("byGroup").setParameter("type", GroupMemberType.Group.name());

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

    public static class GroupMembers implements UserCollectionType {

        private static class ExtraLazyPersistentSet extends PersistentSet {

            ExtraLazyPersistentSet(final SharedSessionContractImplementor session) {
                super(session);
            }

            ExtraLazyPersistentSet(final SharedSessionContractImplementor session, final Set set) {
                super(session, set);
            }

            @Override
            public boolean add(final Object value) {
                if (this.contains(value)) {
                    return false;
                } else {
                    return super.add(value);
                }
            }

            @Override
            public boolean remove(final Object value) {
                if (this.contains(value)) {
                    return super.remove(value);
                } else {
                    return false;
                }
            }
        }

        @Override
        public PersistentCollection instantiate(final SharedSessionContractImplementor session, final CollectionPersister persister) throws HibernateException {
            return new ExtraLazyPersistentSet(session);
        }

        @Override
        public PersistentCollection wrap(final SharedSessionContractImplementor session, final Object collection) {
            return new ExtraLazyPersistentSet(session, (Set<?>) collection);
        }

        @Override
        public Iterator getElementsIterator(final Object collection) {
            return ((Set<?>) collection).iterator();
        }

        @Override
        public boolean contains(final Object collection, final Object entity) {
            return ((Set<?>) collection).contains(entity);
        }

        @Override
        public Object indexOf(final Object collection, final Object entity) {
            throw new UnsupportedOperationException();
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object replaceElements(final Object original, final Object target, final CollectionPersister persister, final Object owner, final Map copyCache, final SharedSessionContractImplementor session) throws HibernateException {
            ((Set<?>) target).clear();
            ((Set<?>) target).addAll((Set) original);
            return target;
        }

        @Override
        public Object instantiate(final int anticipatedSize) {
            return new ExtraLazyPersistentSet(null, new HashSet<>());
        }
    }
}
