package com.neurchi.advisor.common.event.sourcing;

public class EventStoreAppendException extends EventStoreException {

    public EventStoreAppendException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EventStoreAppendException(final String message) {
        super(message);
    }
}
