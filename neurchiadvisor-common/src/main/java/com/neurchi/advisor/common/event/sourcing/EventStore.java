package com.neurchi.advisor.common.event.sourcing;

import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.util.List;

public interface EventStore {

    void appendWith(EventStreamId startingIdentity, List<DomainEvent> events);

    void close();

    List<DispatchableDomainEvent> eventsSince(long lastReceivedEvent);

    EventStream eventStreamSince(EventStreamId identity);

    EventStream fullEventStreamFor(EventStreamId identity);

    void purge(); // mainly used for testing

    void registerEventNotifiable(EventNotifiable eventNotifiable);
}
