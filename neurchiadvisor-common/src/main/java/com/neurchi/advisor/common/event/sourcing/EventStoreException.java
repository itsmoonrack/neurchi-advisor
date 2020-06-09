package com.neurchi.advisor.common.event.sourcing;

public class EventStoreException extends RuntimeException {

    public EventStoreException(final String message) {
        super(message);
    }

    public EventStoreException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
