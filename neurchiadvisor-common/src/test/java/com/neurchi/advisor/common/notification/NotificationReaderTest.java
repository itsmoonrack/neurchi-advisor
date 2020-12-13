package com.neurchi.advisor.common.notification;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.common.event.TestableDomainEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificationReaderTest {

    @Test
    public void TestReadBasicProperties() {
        DomainEvent domainEvent = new TestableDomainEvent(100, "testing");

        Notification notification = new Notification(1, domainEvent);

        NotificationSerializer serializer = NotificationSerializer.instance();

        String serializedNotification = serializer.serialize(notification);

        NotificationReader reader = new NotificationReader(serializedNotification);

        assertEquals(1L, reader.notificationId());
        assertEquals("1", reader.notificationIdAsString());
        assertEquals(domainEvent.occurredOn(), reader.occurredOn());
        assertEquals(notification.typeName(), reader.typeName());
        assertEquals(notification.version(), reader.version());
        assertEquals(domainEvent.eventVersion(), reader.version());
    }
}