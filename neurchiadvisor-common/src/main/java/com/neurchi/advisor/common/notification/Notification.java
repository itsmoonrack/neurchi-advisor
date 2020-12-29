package com.neurchi.advisor.common.notification;

import com.neurchi.advisor.common.AssertionConcern;
import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public final class Notification extends AssertionConcern implements Serializable {

    private DomainEvent event;
    private long notificationId;
    private Instant occurredOn;
    private String typeName;
    private int version;

    public Notification(
            final long notificationId,
            final DomainEvent event) {

        this.setEvent(event);
        this.setNotificationId(notificationId);
        this.setOccurredOn(event.occurredOn());
        this.setTypeName(event.getClass().getName());
        this.setVersion(event.eventVersion());
    }

    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> T event() {
        return (T) this.event;
    }

    public long notificationId() {
        return this.notificationId;
    }

    public Instant occurredOn() {
        return this.occurredOn;
    }

    public String typeName() {
        return this.typeName;
    }

    public int version() {
        return this.version;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Notification that = (Notification) o;
        return notificationId == that.notificationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId);
    }

    @Override
    public String toString() {
        return "Notification[" +
                "event=" + event +
                ", notificationId=" + notificationId +
                ", occurredOn=" + occurredOn +
                ", typeName='" + typeName + '\'' +
                ", version=" + version +
                ']';
    }

    protected Notification() {
        super();
    }

    protected void setEvent(final DomainEvent event) {
        this.assertArgumentNotNull(event, "The event is required.");

        this.event = event;
    }

    protected void setNotificationId(final long notificationId) {
        this.notificationId = notificationId;
    }

    protected void setOccurredOn(final Instant occurredOn) {
        this.occurredOn = occurredOn;
    }

    protected void setTypeName(final String typeName) {
        this.assertArgumentNotEmpty(typeName, "The type name is required.");
        this.assertArgumentLength(typeName, 100, "The type name must be 100 characters or less.");

        this.typeName = typeName;
    }

    private void setVersion(final int version) {
        this.version = version;
    }

}
