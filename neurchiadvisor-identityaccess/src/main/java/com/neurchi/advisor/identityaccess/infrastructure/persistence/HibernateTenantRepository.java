package com.neurchi.advisor.identityaccess.infrastructure.persistence;

import com.neurchi.advisor.common.port.adapter.persistence.hibernate.AbstractHibernateSession;
import com.neurchi.advisor.identityaccess.domain.model.identity.Tenant;
import com.neurchi.advisor.identityaccess.domain.model.identity.TenantId;
import com.neurchi.advisor.identityaccess.domain.model.identity.TenantRepository;
import org.hibernate.NaturalIdLoadAccess;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class HibernateTenantRepository extends AbstractHibernateSession implements TenantRepository {

    @Override
    public void add(final Tenant tenant) {
        try {
            this.session().save(tenant);
        } catch (ConstraintViolationException e) {
            throw new IllegalStateException("Tenant is not unique.", e);
        }
    }

    @Override
    public Optional<Tenant> tenantOfId(final TenantId tenantId) {
        Query<Tenant> query = this.session()
                .createQuery("from Tenant where tenantId = ?1", Tenant.class)
                .setParameter(1, tenantId);

        return query.uniqueResultOptional();
    }

    @Override
    public TenantId nextIdentity() {
        return new TenantId(UUID.randomUUID().toString().toUpperCase());
    }

    @Override
    public void remove(final Tenant tenant) {
        this.session().delete(tenant);
    }

    @Override
    public Optional<Tenant> tenantNamed(final String name) {
        NaturalIdLoadAccess<Tenant> query = this.session()
                .byNaturalId(Tenant.class)
                .using("name", name);

        return query.loadOptional();
    }
}
