package com.neurchi.advisor.common.event;

import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.util.stream.Stream;

public interface EventStore extends AutoCloseable {

    Stream<StoredEvent> allStoredEventsBetween(long lowStoredEventId, long highStoredEventId);

    Stream<StoredEvent> allStoredEventsSince(long storedEventId);

    StoredEvent append(DomainEvent domainEvent);

    @Override
    void close();

    long countStoredEvents();
}
