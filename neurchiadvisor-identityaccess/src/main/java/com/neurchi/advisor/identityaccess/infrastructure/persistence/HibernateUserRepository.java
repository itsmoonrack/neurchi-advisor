package com.neurchi.advisor.identityaccess.infrastructure.persistence;

import com.neurchi.advisor.common.port.adapter.persistence.hibernate.AbstractHibernateSession;
import com.neurchi.advisor.identityaccess.domain.model.identity.TenantId;
import com.neurchi.advisor.identityaccess.domain.model.identity.User;
import com.neurchi.advisor.identityaccess.domain.model.identity.UserRepository;
import org.hibernate.NaturalIdLoadAccess;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public class HibernateUserRepository extends AbstractHibernateSession implements UserRepository {

    @Override
    public void add(final User user) {
        try {
            this.session().save(user);
        } catch (ConstraintViolationException e) {
            throw new IllegalStateException("User is not unique.", e);
        }
    }

    @Override
    public Stream<User> allSimilarlyNamedUsers(final TenantId tenantId, final String firstNamePrefix, final String lastNamePrefix) {

        if (firstNamePrefix.endsWith("%") || lastNamePrefix.endsWith("%")) {
            throw new IllegalArgumentException("Name prefixes must not include %.");
        }

        Query<User> query = this.session().createQuery(
                "from User " +
                        "where tenantId = ?1 " +
                        "and person.name.firstName like ?2 " +
                        "and person.name.lastName like ?3", User.class);

        query.setParameter(1, tenantId);
        query.setParameter(2, firstNamePrefix + "%", StandardBasicTypes.STRING);
        query.setParameter(3, lastNamePrefix + "%", StandardBasicTypes.STRING);

        return query.stream();
    }

    @Override
    public void remove(final User user) {
        this.session().delete(user);
    }

    @Override
    public Optional<User> userWithUsername(final TenantId tenantId, final String username) {
        NaturalIdLoadAccess<User> query = this.session()
                .byNaturalId(User.class)
                .using("tenantId", tenantId)
                .using("username", username);

        return query.loadOptional();
    }
}
