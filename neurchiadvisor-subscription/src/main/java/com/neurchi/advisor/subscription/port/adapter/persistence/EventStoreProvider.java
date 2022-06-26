package com.neurchi.advisor.subscription.port.adapter.persistence;

import com.neurchi.advisor.common.event.sourcing.EventStore;

public class EventStoreProvider {

    private EventStore eventStore;

    public static EventStoreProvider instance() {
        return new EventStoreProvider();
    }

    public EventStore eventStore() {
        return this.eventStore;
    }
}
