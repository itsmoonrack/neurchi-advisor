package com.neurchi.advisor.common.event;

import com.neurchi.advisor.common.CommonTestCase;
import com.neurchi.advisor.common.persistence.PersistenceManagerProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventStoreContractTest extends CommonTestCase {

    @Test
    public void TestAllStoredEventsBetween() throws Exception {
        EventStore eventStore = this.eventStore();

        long totalEvents = eventStore.countStoredEvents();

        assertEquals(totalEvents, eventStore.allStoredEventsBetween(1, totalEvents).count());

        assertEquals(10, eventStore.allStoredEventsBetween(totalEvents - 9, totalEvents).count());
    }

    @Test
    public void TestAllStoredEventsSince() throws Exception {
        EventStore eventStore = this.eventStore();

        long totalEvents = eventStore.countStoredEvents();

        assertEquals(totalEvents, eventStore.allStoredEventsSince(0).count());

        assertEquals(0, eventStore.allStoredEventsSince(totalEvents).count());

        assertEquals(10, eventStore.allStoredEventsSince(totalEvents - 10).count());
    }

    @Test
    public void TestAppend() throws Exception {
        EventStore eventStore = this.eventStore();

        long numberOfEvents = eventStore.countStoredEvents();

        TestableDomainEvent domainEvent = new TestableDomainEvent(10001, "testDomainEvent");

        StoredEvent storedEvent = eventStore.append(domainEvent);

        assertTrue(eventStore.countStoredEvents() > numberOfEvents);
        assertEquals(eventStore.countStoredEvents(), numberOfEvents + 1);

        assertNotNull(storedEvent);

        TestableDomainEvent reconstitutedDomainEvent = storedEvent.toDomainEvent();

        assertNotNull(reconstitutedDomainEvent);
        assertEquals(domainEvent.id(), reconstitutedDomainEvent.id());
        assertEquals(domainEvent.name(), reconstitutedDomainEvent.name());
        assertEquals(domainEvent.occurredOn(), reconstitutedDomainEvent.occurredOn());
    }

    @Test
    public void TestCountStoredEvents() throws Exception {
        EventStore eventStore = this.eventStore();

        long numberOfEvents = eventStore.countStoredEvents();

        TestableDomainEvent lastDomainEvent = null;

        for (int index = 0; index < 10; ++index) {
            TestableDomainEvent domainEvent = new TestableDomainEvent(10001 + index, "testDomainEvent" + index);

            lastDomainEvent = domainEvent;

            eventStore.append(domainEvent);
        }

        assertEquals(numberOfEvents + 10, eventStore.countStoredEvents());

        numberOfEvents = eventStore.countStoredEvents();

        assertEquals(1, eventStore.allStoredEventsBetween(numberOfEvents, numberOfEvents + 1000).count());

        StoredEvent storedEvent = eventStore.allStoredEventsBetween(numberOfEvents, numberOfEvents).findFirst().orElse(null);

        assertNotNull(storedEvent);

        TestableDomainEvent reconstitutedDomainEvent = storedEvent.toDomainEvent();

        assertNotNull(reconstitutedDomainEvent);
        assertEquals(lastDomainEvent.id(), reconstitutedDomainEvent.id());
        assertEquals(lastDomainEvent.name(), reconstitutedDomainEvent.name());
        assertEquals(lastDomainEvent.occurredOn(), reconstitutedDomainEvent.occurredOn());
    }

    @Test
    public void TestStoredEvent() throws Exception {
        EventStore eventStore = this.eventStore();

        TestableDomainEvent domainEvent = new TestableDomainEvent(10001, "testDomainEvent");

        StoredEvent storedEvent = eventStore.append(domainEvent);

        assertNotNull(storedEvent);

        TestableDomainEvent reconstitutedDomainEvent = storedEvent.toDomainEvent();

        assertNotNull(reconstitutedDomainEvent);
        assertEquals(domainEvent.id(), reconstitutedDomainEvent.id());
        assertEquals(domainEvent.name(), reconstitutedDomainEvent.name());
        assertEquals(domainEvent.occurredOn(), reconstitutedDomainEvent.occurredOn());
    }

    private EventStore eventStore() {
        EventStore eventStore = new MockEventStore(new PersistenceManagerProvider() {});

        assertNotNull(eventStore);

        return eventStore;
    }
}
