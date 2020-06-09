package com.neurchi.advisor.common.event.sourcing;

public class EventStoreVersionException extends EventStoreException {

    public EventStoreVersionException(final String message) {
        super(message);
    }

    public EventStoreVersionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
