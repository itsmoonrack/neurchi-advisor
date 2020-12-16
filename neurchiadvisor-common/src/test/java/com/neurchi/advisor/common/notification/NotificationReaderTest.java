package com.neurchi.advisor.common.notification;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.common.event.TestableDomainEvent;
import com.neurchi.advisor.common.event.TestableNavigableDomainEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void TestReadDomainEventProperties() {
        TestableDomainEvent domainEvent = new TestableDomainEvent(100, "testing");

        Notification notification = new Notification(1, domainEvent);

        NotificationSerializer serializer = NotificationSerializer.instance();

        String serializedNotification = serializer.serialize(notification);

        NotificationReader reader = new NotificationReader(serializedNotification);

        assertEquals("" + domainEvent.eventVersion(), reader.eventStringValue("eventVersion"));
        assertEquals("" + domainEvent.eventVersion(), reader.eventStringValue("/eventVersion"));
        assertEquals("" + domainEvent.id(), reader.eventStringValue("id"));
        assertEquals("" + domainEvent.id(), reader.eventStringValue("/id"));
        assertEquals("" + domainEvent.name(), reader.eventStringValue("name"));
        assertEquals("" + domainEvent.name(), reader.eventStringValue("/name"));
        assertEquals("" + domainEvent.occurredOn(), reader.eventStringValue("occurredOn"));
        assertEquals("" + domainEvent.occurredOn(), reader.eventStringValue("/occurredOn"));
    }

    @Test
    public void TestReadNestedDomainEventProperties() {
        TestableNavigableDomainEvent domainEvent = new TestableNavigableDomainEvent(100, "testing");

        Notification notification = new Notification(1, domainEvent);

        NotificationSerializer serializer = NotificationSerializer.instance();

        String serializedNotification = serializer.serialize(notification);

        NotificationReader reader = new NotificationReader(serializedNotification);

        assertEquals("" + domainEvent.eventVersion(), reader.eventStringValue("eventVersion"));
        assertEquals("" + domainEvent.eventVersion(), reader.eventStringValue("/eventVersion"));
        assertEquals(domainEvent.eventVersion(), reader.eventIntegerValue("eventVersion").intValue());
        assertEquals(domainEvent.eventVersion(), reader.eventIntegerValue("/eventVersion").intValue());
        assertEquals("" + domainEvent.nestedEvent().eventVersion(), reader.eventStringValue("nestedEvent", "eventVersion"));
        assertEquals("" + domainEvent.nestedEvent().eventVersion(), reader.eventStringValue("/nestedEvent/eventVersion"));
        assertEquals(domainEvent.nestedEvent().eventVersion(), reader.eventIntegerValue("nestedEvent", "eventVersion").intValue());
        assertEquals(domainEvent.nestedEvent().eventVersion(), reader.eventIntegerValue("/nestedEvent/eventVersion").intValue());
        assertEquals("" + domainEvent.nestedEvent().id(), reader.eventStringValue("nestedEvent", "id"));
        assertEquals("" + domainEvent.nestedEvent().id(), reader.eventStringValue("/nestedEvent/id"));
        assertEquals(domainEvent.nestedEvent().id(), reader.eventLongValue("nestedEvent", "id").longValue());
        assertEquals(domainEvent.nestedEvent().id(), reader.eventLongValue("/nestedEvent/id").longValue());
        assertEquals("" + domainEvent.nestedEvent().name(), reader.eventStringValue("nestedEvent", "name"));
        assertEquals("" + domainEvent.nestedEvent().name(), reader.eventStringValue("/nestedEvent/name"));
        assertEquals("" + domainEvent.nestedEvent().occurredOn(), reader.eventStringValue("nestedEvent", "occurredOn"));
        assertEquals("" + domainEvent.nestedEvent().occurredOn(), reader.eventStringValue("/nestedEvent/occurredOn"));
        assertEquals(domainEvent.nestedEvent().occurredOn(), reader.eventInstantValue("nestedEvent", "occurredOn"));
        assertEquals(domainEvent.nestedEvent().occurredOn(), reader.eventInstantValue("/nestedEvent/occurredOn"));
        assertEquals("" + domainEvent.occurredOn(), reader.eventStringValue("occurredOn"));
        assertEquals("" + domainEvent.occurredOn(), reader.eventStringValue("/occurredOn"));
        assertEquals(domainEvent.occurredOn(), reader.eventInstantValue("occurredOn"));
        assertEquals(domainEvent.occurredOn(), reader.eventInstantValue("/occurredOn"));
    }

    @Test
    public void TestDotNotation() {
        TestableNavigableDomainEvent domainEvent = new TestableNavigableDomainEvent(100, "testing");

        Notification notification = new Notification(1, domainEvent);

        NotificationSerializer serializer = NotificationSerializer.instance();

        String serializedNotification = serializer.serialize(notification);

        NotificationReader reader = new NotificationReader(serializedNotification);

        assertEquals("" + domainEvent.nestedEvent().eventVersion(), reader.eventStringValue("nestedEvent.eventVersion"));
        assertEquals(domainEvent.nestedEvent().eventVersion(), reader.eventIntegerValue("nestedEvent.eventVersion").intValue());
    }

    @Test
    public void TestReadBogusProperties() {
        TestableNavigableDomainEvent domainEvent = new TestableNavigableDomainEvent(100L, "testing");

        Notification notification = new Notification(1, domainEvent);

        NotificationSerializer serializer = NotificationSerializer.instance();

        String serializedNotification = serializer.serialize(notification);

        NotificationReader reader = new NotificationReader(serializedNotification);

        assertThrows(IllegalArgumentException.class, () -> reader.eventStringValue("eventVersion.version"));
    }

    @Test
    public void TestReadNullProperties() {
        TestableNullPropertyDomainEvent domainEvent = new TestableNullPropertyDomainEvent(100L, "testingNulls");

        Notification notification = new Notification(1, domainEvent);

        NotificationSerializer serializer = NotificationSerializer.instance();

        String serializedNotification = serializer.serialize(notification);

        NotificationReader reader = new NotificationReader(serializedNotification);

        assertNull(reader.eventStringValue("textMustBeNull"));

        assertNull(reader.eventStringValue("textMustBeNull2"));

        assertNull(reader.eventIntegerValue("numberMustBeNull"));

        assertNull(reader.eventStringValue("nested.nestedTextMustBeNull"));

        assertNull(reader.eventStringValue("nullNested.nestedTextMustBeNull"));

        assertNull(reader.eventStringValue("nested.nestedDeeply.nestedDeeplyTextMustBeNull"));

        assertNull(reader.eventStringValue("nested.nestedDeeply.nestedDeeplyTextMustBeNull2"));

        assertNull(reader.eventStringValue("nested.nullNestedDeeply.nestedDeeplyTextMustBeNull"));

        assertNull(reader.eventStringValue("nested.nullNestedDeeply.nestedDeeplyTextMustBeNull2"));
    }
}