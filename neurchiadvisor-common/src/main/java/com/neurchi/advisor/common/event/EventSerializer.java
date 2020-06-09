package com.neurchi.advisor.common.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.common.serializer.AbstractSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public final class EventSerializer extends AbstractSerializer {

    private static EventSerializer eventSerializer;

    public static synchronized EventSerializer instance() {
        if (EventSerializer.eventSerializer == null) {
            EventSerializer.eventSerializer = new EventSerializer();
        }

        return EventSerializer.eventSerializer;
    }

    public EventSerializer(final boolean isCompact) {
        this(false, isCompact);
    }

    public EventSerializer(boolean isPretty, boolean isCompact) {
        super(isPretty, isCompact);
    }

    public String serialize(final DomainEvent domainEvent) {
        try {
            return this.objectMapper().writeValueAsString(domainEvent);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to serialize domain event.", e);
        }
    }

    public <T extends DomainEvent> T deserialize(final InputStream source, final Class<T> type) {
        try {
            return this.objectMapper().readValue(source, type);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to deserialize domain event.", e);
        }
    }

    public <T extends DomainEvent> T deserialize(final Reader source, final Class<T> type) {
        try {
            return this.objectMapper().readValue(source, type);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to deserialize domain event.", e);
        }
    }

    public <T extends DomainEvent> T deserialize(final String content, final Class<T> type) {
        try {
            return this.objectMapper().readValue(content, type);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to deserialize domain event.", e);
        }
    }

    private EventSerializer() {
        this(false, false);
    }
}
