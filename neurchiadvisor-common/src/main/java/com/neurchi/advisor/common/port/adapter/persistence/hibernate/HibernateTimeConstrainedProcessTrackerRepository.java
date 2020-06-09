package com.neurchi.advisor.common.port.adapter.persistence.hibernate;

import com.neurchi.advisor.common.domain.model.process.ProcessId;
import com.neurchi.advisor.common.domain.model.process.TimeConstrainedProcessTracker;
import com.neurchi.advisor.common.domain.model.process.TimeConstrainedProcessTrackerRepository;
import com.neurchi.advisor.common.persistence.PersistenceManagerProvider;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

public class HibernateTimeConstrainedProcessTrackerRepository
        extends AbstractHibernateSession
        implements TimeConstrainedProcessTrackerRepository {

    HibernateTimeConstrainedProcessTrackerRepository() {
        super();
    }

    HibernateTimeConstrainedProcessTrackerRepository(
            final PersistenceManagerProvider persistenceManagerProvider) {

        if (!persistenceManagerProvider.hasHibernateSession()) {
            throw new IllegalArgumentException("The PersistenceManagerProvider must have a Hibernate Session.");
        }

        this.setSession(persistenceManagerProvider.hibernateSession());
    }

    @Override
    public void add(final TimeConstrainedProcessTracker timeConstrainedProcessTracker) {
        this.save(timeConstrainedProcessTracker);
    }

    @Override
    public Stream<TimeConstrainedProcessTracker> allTimedOut() {
        Query<TimeConstrainedProcessTracker> query =
                this.session().createQuery(
                        "from TimeConstrainedProcessTracker " +
                                "where completed = false " +
                                "  and processInformedOfTimeout = false " +
                                "  and timeoutOccursOn <= ?1",
                        TimeConstrainedProcessTracker.class);

        query.setParameter(1, Instant.now());

        return query.stream();
    }

    @Override
    public Stream<TimeConstrainedProcessTracker> allTimedOutOf(final String tenantId) {
        Query<TimeConstrainedProcessTracker> query =
                this.session().createQuery(
                        "from TimeConstrainedProcessTracker " +
                                "where tenantId = ?1 " +
                                "  and completed = false " +
                                "  and processInformedOfTimeout = false " +
                                "  and timeoutOccursOn <= ?2",
                        TimeConstrainedProcessTracker.class);

        query.setParameter(1, tenantId);
        query.setParameter(2, Instant.now());

        return query.stream();
    }

    @Override
    public Stream<TimeConstrainedProcessTracker> allTrackers(final String tenantId) {
        Query<TimeConstrainedProcessTracker> query =
                this.session().createQuery(
                        "from TimeConstrainedProcessTracker where tenantId = ?1",
                        TimeConstrainedProcessTracker.class);

        query.setParameter(1, tenantId);

        return query.stream();
    }

    @Override
    public void save(final TimeConstrainedProcessTracker timeConstrainedProcessTracker) {
        try {
            this.session().saveOrUpdate(timeConstrainedProcessTracker);
        } catch (ConstraintViolationException e) {
            throw new IllegalStateException("Either TimeConstrainedProcessTracker is not unique or another constraint has been violated.", e);
        }
    }

    @Override
    public Optional<TimeConstrainedProcessTracker> trackerOfProcessId(final String tenantId, final ProcessId processId) {
        Query<TimeConstrainedProcessTracker> query =
                this.session().createQuery(
                        "from TimeConstrainedProcessTracker " +
                                "where tenantId = ?1 and processId = ?2",
                        TimeConstrainedProcessTracker.class);

        query.setParameter(1, tenantId);
        query.setParameter(2, processId);

        return query.uniqueResultOptional();
    }
}
