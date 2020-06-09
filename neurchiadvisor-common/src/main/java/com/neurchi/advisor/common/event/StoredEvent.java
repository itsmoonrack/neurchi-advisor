package com.neurchi.advisor.common.event;

import com.neurchi.advisor.common.AssertionConcern;
import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public class StoredEvent extends AssertionConcern {

    private String eventBody;
    private long eventId;
    private Instant occurredOn;
    private String typeName;

    public StoredEvent(final String typeName, final Instant occurredOn, final String eventBody) {
        this.setEventBody(eventBody);
        this.setOccurredOn(occurredOn);
        this.setTypeName(typeName);
    }

    public StoredEvent(final String typeName, final Instant occurredOn, final String eventBody, final long eventId) {
        this(typeName, occurredOn, eventBody);

        this.setEventId(eventId);
    }

    public String eventBody() {
        return this.eventBody;
    }

    public long eventId() {
        return this.eventId;
    }

    public Instant occurredOn() {
        return this.occurredOn;
    }

    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> T toDomainEvent() {
        final Class<T> domainEventClass;

        try {
            domainEventClass = (Class<T>) Class.forName(this.typeName());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to convert to domain event.", e);
        }

        return EventSerializer
                .instance()
                .deserialize(this.eventBody(), domainEventClass);
    }

    public String typeName() {
        return this.typeName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StoredEvent that = (StoredEvent) o;
        return eventId == that.eventId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "StoredEvent{" +
                "eventBody='" + eventBody + '\'' +
                ", eventId=" + eventId +
                ", occurredOn=" + occurredOn +
                ", typeName='" + typeName + '\'' +
                '}';
    }

    public StoredEvent() {
        super();

        this.setEventId(-1);
    }

    protected void setEventBody(final String eventBody) {
        this.assertArgumentNotEmpty(eventBody, "The event body is required.");
        this.assertArgumentLength(eventBody, 1, 65000, "The event body must be 65000 characters or less.");

        this.eventBody = eventBody;
    }

    protected void setEventId(final long eventId) {
        this.eventId = eventId;
    }

    protected void setOccurredOn(final Instant occurredOn) {
        this.occurredOn = occurredOn;
    }

    protected void setTypeName(final String typeName) {
        this.assertArgumentNotEmpty(typeName, "The event type name is required.");
        this.assertArgumentLength(typeName, 1, 100, "The event type name must be 100 characters or less.");

        this.typeName = typeName;
    }

}
