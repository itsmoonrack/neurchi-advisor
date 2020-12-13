package com.neurchi.advisor.common.event;

import com.neurchi.advisor.common.AssertionConcern;
import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.common.persistence.PersistenceManagerProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class MockEventStore extends AssertionConcern implements EventStore {

    private static final long START_ID = 789;

    private List<StoredEvent> storedEvents;

    public MockEventStore(final PersistenceManagerProvider persistenceManagerProvider) {

        this.assertArgumentNotNull(
                persistenceManagerProvider,
                "PersistenceManagerProvider must be provided.");

        // always start with at least 21 events

        this.storedEvents = new ArrayList<>();

        int numberOfStoredEvents = ThreadLocalRandom.current().nextInt(21, 1000);

        for (int index = 0; index < numberOfStoredEvents; ++index) {
            StoredEvent storedEvent = this.newStoredEvent(START_ID + index, index + 1);

            this.storedEvents.add(storedEvent);
        }
    }

    @Override
    public Stream<StoredEvent> allStoredEventsBetween(final long lowStoredEventId, final long highStoredEventId) {
        return this.storedEvents.stream().filter(storedEvent -> storedEvent.eventId() >= lowStoredEventId && storedEvent.eventId() <= highStoredEventId);
    }

    @Override
    public Stream<StoredEvent> allStoredEventsSince(final long storedEventId) {
        return this.allStoredEventsBetween(storedEventId + 1, this.countStoredEvents());
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

        storedEvent.setEventId(this.storedEvents.size() + 1);

        this.storedEvents.add(storedEvent);

        return storedEvent;
    }

    @Override
    public void close() {

    }

    @Override
    public long countStoredEvents() {
        return this.storedEvents.size();
    }

    private StoredEvent newStoredEvent(final long domainEventId, final long storedEventId) {
        EventSerializer serializer = EventSerializer.instance();

        DomainEvent event = new TestableDomainEvent(domainEventId, "name" + domainEventId);
        String serializedEvent = serializer.serialize(event);
        StoredEvent storedEvent = new StoredEvent(event.getClass().getName(), event.occurredOn(), serializedEvent);
        storedEvent.setEventId(storedEventId);

        return storedEvent;
    }
}
