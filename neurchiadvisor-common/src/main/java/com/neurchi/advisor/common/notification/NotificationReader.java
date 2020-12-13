package com.neurchi.advisor.common.notification;

import com.fasterxml.jackson.databind.JsonNode;
import com.neurchi.advisor.common.media.AbstractJSONMediaReader;

import java.time.Instant;

public class NotificationReader extends AbstractJSONMediaReader {

    private JsonNode event;

    public NotificationReader(final String jsonRepresentation) {
        super(jsonRepresentation);

        this.setEvent(this.representation().get("event"));
    }

    public NotificationReader(final JsonNode representationNode) {
        super(representationNode);

        this.setEvent(this.representation().get("event"));
    }

    public String eventStringValue(final String... keys) {
        return this.stringValue(this.event(), keys);
    }

    public Integer eventIntegerValue(final String... keys) {
        String stringValue = this.stringValue(this.event(), keys);
        return stringValue == null ? null : Integer.parseInt(stringValue);
    }

    public Boolean eventBooleanValue(final String... keys) {
        String stringValue = this.stringValue(this.event(), keys);
        return stringValue == null ? null : Boolean.parseBoolean(stringValue);
    }

    public Instant eventInstantValue(final String... keys) {
        String stringValue = this.stringValue(this.event(), keys);
        return stringValue == null ? null : Instant.parse(stringValue);
    }

    public long notificationId() {
        return this.longValue("notificationId");
    }

    public String notificationIdAsString() {
        return this.stringValue("notificationId");
    }

    public Instant occurredOn() {
        return this.instantValue("occurredOn");
    }

    public String typeName() {
        return this.stringValue("typeName");
    }

    public int version() {
        return this.integerValue("version");
    }

    private JsonNode event() {
        return this.event;
    }

    private void setEvent(final JsonNode event) {
        this.event = event;
    }
}
