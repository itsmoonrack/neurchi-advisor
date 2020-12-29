package com.neurchi.advisor.common.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventSerializerTest {

    @Test
    public void TestDefaultFormat() {
        EventSerializer serializer = EventSerializer.instance();

        String serializedEvent = serializer.serialize(new TestableDomainEvent(1, null));

        assertTrue(serializedEvent.contains("\"id\""));
        assertTrue(serializedEvent.contains("\"occurredOn\""));
        assertFalse(serializedEvent.contains("\n"));
        assertTrue(serializedEvent.contains("null"));
    }

    @Test
    public void TestCompact() {
        EventSerializer serializer = new EventSerializer(true);

        String serializedEvent = serializer.serialize(new TestableDomainEvent(1, null));

        assertTrue(serializedEvent.contains("\"id\""));
        assertTrue(serializedEvent.contains("\"occurredOn\""));
        assertFalse(serializedEvent.contains("\n"));
        assertFalse(serializedEvent.contains("null"));
    }

    @Test
    public void TestPrettyAndCompact() {
        EventSerializer serializer = new EventSerializer(true, true);

        String serializedEvent = serializer.serialize(new TestableDomainEvent(1, null));

        assertTrue(serializedEvent.contains("\"id\""));
        assertTrue(serializedEvent.contains("\"occurredOn\""));
        assertTrue(serializedEvent.contains("\n"));
        assertFalse(serializedEvent.contains("null"));
    }

    @Test
    public void TestDeserializeDefault() {
        EventSerializer serializer = EventSerializer.instance();

        String serializedEvent = serializer.serialize(new TestableDomainEvent(1, null));

        TestableDomainEvent event = serializer.deserialize(serializedEvent, TestableDomainEvent.class);

        assertTrue(serializedEvent.contains("null"));
        assertEquals(1, event.id());
        assertNull(event.name());
        assertNotNull(event.occurredOn());
    }

    @Test
    public void TestDeserializedCompactNotNull() {
        EventSerializer serializer = new EventSerializer(true);

        String serializedEvent = serializer.serialize(new TestableDomainEvent(1, "test"));

        TestableDomainEvent event = serializer.deserialize(serializedEvent, TestableDomainEvent.class);

        assertFalse(serializedEvent.contains("null"));
        assertTrue(serializedEvent.contains("\"test\""));
        assertEquals(1, event.id());
        assertEquals("test", event.name());
        assertNotNull(event.occurredOn());
    }

    @Test
    public void TestDeserializedCompactNull() {
        EventSerializer serializer = new EventSerializer(true);

        String serializedEvent = serializer.serialize(new TestableDomainEvent(1, null));

        TestableDomainEvent event = serializer.deserialize(serializedEvent, TestableDomainEvent.class);

        assertFalse(serializedEvent.contains("null"));
        assertEquals(1, event.id());
        assertNull(event.name());
        assertNotNull(event.occurredOn());
    }

    @Test
    public void TestDeserializePrettyAndCompactNull() {
        EventSerializer serializer = new EventSerializer(true, true);

        String serializedEvent = serializer.serialize(new TestableDomainEvent(1, null));

        TestableDomainEvent event = serializer.deserialize(serializedEvent, TestableDomainEvent.class);

        assertFalse(serializedEvent.contains("null"));
        assertTrue(serializedEvent.contains("\n"));
        assertEquals(1, event.id());
        assertNull(event.name());
        assertNotNull(event.occurredOn());
    }
}