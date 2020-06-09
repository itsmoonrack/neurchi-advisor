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

    public String eventTextValue(final String... keys) {
        return this.textValue(this.event(), keys);
    }

    public Integer eventIntegerValue(final String... keys) {
        String textValue = this.textValue(this.event(), keys);
        return textValue == null ? null : Integer.parseInt(textValue);
    }

    public Boolean eventBooleanValue(final String... keys) {
        String textValue = this.textValue(this.event(), keys);
        return textValue == null ? null : Boolean.parseBoolean(textValue);
    }

    public long notificationId() {
        return this.longValue("notificationId");
    }

    public String notificationIdAsString() {
        return this.textValue("notificationId");
    }

    public Instant occurredOn() {
        return this.instantValue("occurredOn");
    }

    public String typeName() {
        return this.textValue("typeName");
    }

    private JsonNode event() {
        return this.event;
    }

    private void setEvent(final JsonNode event) {
        this.event = event;
    }
}
