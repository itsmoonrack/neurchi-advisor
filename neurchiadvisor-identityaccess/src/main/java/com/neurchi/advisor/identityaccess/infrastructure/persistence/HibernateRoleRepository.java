package com.neurchi.advisor.identityaccess.infrastructure.persistence;

import com.neurchi.advisor.common.port.adapter.persistence.hibernate.AbstractHibernateSession;
import com.neurchi.advisor.identityaccess.domain.model.access.Role;
import com.neurchi.advisor.identityaccess.domain.model.access.RoleRepository;
import com.neurchi.advisor.identityaccess.domain.model.identity.TenantId;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public class HibernateRoleRepository extends AbstractHibernateSession implements RoleRepository {

    @Override
    public void add(final Role role) {
        try {
            this.session().save(role);
        } catch (ConstraintViolationException e) {
            throw new IllegalStateException("Role is not unique.", e);
        }
    }

    @Override
    public Stream<Role> allRoles(final TenantId tenantId) {
        Query<Role> query = this.session()
                .createQuery("from Role where tenantId = ?1", Role.class);

        query.setParameter(1, tenantId);

        return query.stream();
    }

    @Override
    public void remove(final Role role) {
        this.session().delete(role);
    }

    @Override
    public Optional<Role> roleNamed(final TenantId tenantId, final String roleName) {
        Query<Role> query = this.session()
                .createQuery("from Role where tenantId = ?1 and name = ?2", Role.class);

        query.setParameter(1, tenantId);
        query.setParameter(2, roleName);

        return query.uniqueResultOptional();
    }
}
