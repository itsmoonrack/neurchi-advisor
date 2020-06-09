package com.neurchi.advisor.common.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.neurchi.advisor.common.serializer.AbstractSerializer;

import java.io.IOException;

public final class NotificationSerializer extends AbstractSerializer {

    private static NotificationSerializer notificationSerializer;

    public static synchronized NotificationSerializer instance() {
        if (NotificationSerializer.notificationSerializer == null) {
            NotificationSerializer.notificationSerializer = new NotificationSerializer();
        }

        return NotificationSerializer.notificationSerializer;
    }

    public NotificationSerializer(final boolean isCompact) {
        this(false, isCompact);
    }

    public NotificationSerializer(final boolean isPretty, final boolean isCompact) {
        super(isPretty, isCompact);
    }

    public String serialize(final Notification notification) {
        try {
            return this.objectMapper().writeValueAsString(notification);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to serialize notification.", e);
        }
    }

    public <T extends Notification> T deserialize(final String content, final Class<T> type) {
        try {
            return this.objectMapper().readValue(content, type);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to deserialize notification.", e);
        }
    }

    private NotificationSerializer() {
        this(false, false);
    }
}
