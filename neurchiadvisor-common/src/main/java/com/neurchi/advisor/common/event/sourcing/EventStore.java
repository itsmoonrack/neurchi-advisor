package com.neurchi.advisor.common.event.sourcing;

import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.util.List;

public interface EventStore extends AutoCloseable {

    void appendWith(EventStreamId startingIdentity, List<DomainEvent> events);

    List<DispatchableDomainEvent> eventsSince(long lastReceivedEvent);

    EventStream eventStreamSince(EventStreamId identity);

    EventStream fullEventStreamFor(EventStreamId identity);

    void registerEventNotifiable(EventNotifiable eventNotifiable);
}
