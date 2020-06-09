package com.neurchi.advisor.common.port.adapter.persistence.eventsourcing;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.common.event.sourcing.EventStream;

import java.util.List;
import java.util.stream.Stream;

public final class DefaultEventStream implements EventStream {

    private final List<DomainEvent> events;
    private final int version;

    public DefaultEventStream(final List<DomainEvent> events, final int version) {
        this.events = events;
        this.version = version;
    }

    @Override
    public Stream<DomainEvent> events() {
        return this.events.stream();
    }

    @Override
    public int version() {
        return this.version;
    }

}
