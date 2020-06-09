package com.neurchi.advisor.common.port.adapter.persistence.hibernate;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.common.event.EventSerializer;
import com.neurchi.advisor.common.event.EventStore;
import com.neurchi.advisor.common.event.StoredEvent;
import com.neurchi.advisor.common.persistence.PersistenceManagerProvider;
import org.hibernate.query.Query;

import java.util.stream.Stream;

public class HibernateEventStore extends AbstractHibernateSession implements EventStore {

    public HibernateEventStore(final PersistenceManagerProvider persistenceManagerProvider) {

        if (!persistenceManagerProvider.hasHibernateSession()) {
            throw new IllegalArgumentException("The PersistenceManagerProvider must have a Hibernate session.");
        }

        this.setSession(persistenceManagerProvider.hibernateSession());
    }

    public HibernateEventStore() {
        super();
    }

    @Override
    public Stream<StoredEvent> allStoredEventsBetween(final long lowStoredEventId, final long highStoredEventId) {
        Query<StoredEvent> query =
                this.session().createQuery(
                        "from StoredEvent " +
                                "where eventId between ?1 and ?2 " +
                                "order by eventId",
                        StoredEvent.class);

        query.setParameter(1, lowStoredEventId);
        query.setParameter(2, highStoredEventId);

        return query.stream();
    }

    @Override
    public Stream<StoredEvent> allStoredEventsSince(final long storedEventId) {
        Query<StoredEvent> query =
                this.session().createQuery(
                        "from StoredEvent " +
                                "where eventId > ?1 " +
                                "order by eventId",
                        StoredEvent.class);

        query.setParameter(1, storedEventId);

        return query.stream();
    }

    @Override
    public StoredEvent append(final DomainEvent domainEvent) {
        String eventSerialization =
                EventSerializer.instance().serialize(domainEvent);

        StoredEvent storedEvent =
                new StoredEvent(
                        domainEvent.getClass().getName(),
                        domainEvent.occurredOn(),
                        eventSerialization);

        this.session().save(storedEvent);

        return storedEvent;
    }

    @Override
    public void close() {
        // no-op
    }

    @Override
    public long countStoredEvents() {
        Query<Long> query =
                this.session().createQuery(
                        "select count(*) from StoredEvent",
                        Long.class);

        return query.getSingleResult();
    }
}
