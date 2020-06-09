package com.neurchi.advisor.common.event.sourcing;

import com.neurchi.advisor.common.domain.model.DomainEvent;

public final class DispatchableDomainEvent {

    private final DomainEvent domainEvent;
    private final long eventId;

    public DispatchableDomainEvent(final DomainEvent domainEvent, final long eventId) {
        this.domainEvent = domainEvent;
        this.eventId = eventId;
    }

    public DomainEvent domainEvent() {
        return this.domainEvent;
    }

    public long eventId() {
        return this.eventId;
    }
}
