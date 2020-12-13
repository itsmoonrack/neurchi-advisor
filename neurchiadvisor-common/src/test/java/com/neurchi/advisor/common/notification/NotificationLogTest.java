package com.neurchi.advisor.common.notification;

import com.neurchi.advisor.common.CommonTestCase;
import com.neurchi.advisor.common.event.EventStore;
import com.neurchi.advisor.common.event.MockEventStore;
import com.neurchi.advisor.common.persistence.PersistenceManagerProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationLogTest extends CommonTestCase {

    @Test
    public void TestCurrentNotificationLogFromFactory() {
        EventStore eventStore = this.eventStore();
        NotificationLogFactory factory = new NotificationLogFactory(eventStore);
        NotificationLog log = factory.createCurrentNotificationLog();

        assertTrue(NotificationLogFactory.notificationsPerLog() >= log.totalNotifications());
        assertTrue(eventStore.countStoredEvents() >= log.totalNotifications());
        assertFalse(log.hasNextNotificationLog());
        assertTrue(log.hasPreviousNotificationLog());
        assertFalse(log.isArchived());
    }

    @Test
    public void TestFirstNotificationLogFromFactory() {
        EventStore eventStore = this.eventStore();
        NotificationLogId id = NotificationLogId.first(NotificationLogFactory.notificationsPerLog());
        NotificationLogFactory factory = new NotificationLogFactory(eventStore);
        NotificationLog log = factory.createNotificationLog(id);

        assertEquals(NotificationLogFactory.notificationsPerLog(), log.totalNotifications());
        assertTrue(eventStore.countStoredEvents() >= log.totalNotifications());
        assertTrue(log.hasNextNotificationLog());
        assertFalse(log.hasPreviousNotificationLog());
        assertTrue(log.isArchived());
    }

    @Test
    public void TestPreviousOfCurrentNotificationLogFromFactory() {
        EventStore eventStore = this.eventStore();
        long totalEvents = eventStore.countStoredEvents();
        boolean shouldBePrevious = totalEvents > (NotificationLogFactory.notificationsPerLog() * 2L);
        NotificationLogFactory factory = new NotificationLogFactory(eventStore);
        NotificationLog log = factory.createCurrentNotificationLog();

        NotificationLogId previousId = log.decodedPreviousNotificationLogId();
        log = factory.createNotificationLog(previousId);

        assertEquals(NotificationLogFactory.notificationsPerLog(), log.totalNotifications());
        assertTrue(totalEvents >= log.totalNotifications());
        assertTrue(log.hasNextNotificationLog());
        assertEquals(shouldBePrevious, log.hasPreviousNotificationLog());
        assertTrue(log.isArchived());
    }

    @Test
    public void TestEncodedWithDecodedNavigationIds() {
        EventStore eventStore = this.eventStore();
        NotificationLogFactory factory = new NotificationLogFactory(eventStore);
        NotificationLog log = factory.createCurrentNotificationLog();

        String currentId = log.notificationLogId();
        NotificationLogId decodedCurrentLogId = log.decodedNotificationLogId();
        assertEquals(log.decodedNotificationLogId(), new NotificationLogId(currentId));

        String previousId = log.previousNotificationLogId();
        NotificationLogId decodedPreviousLogId = log.decodedPreviousNotificationLogId();
        assertEquals(decodedPreviousLogId, new NotificationLogId(previousId));
        log = factory.createNotificationLog(log.decodedPreviousNotificationLogId());

        String nextId = log.nextNotificationLogId();
        NotificationLogId decodedNextLogId = log.decodedNextNotificationLogId();
        assertEquals(decodedNextLogId, new NotificationLogId(nextId));
        assertEquals(decodedCurrentLogId, decodedNextLogId);
    }

    private EventStore eventStore() {
        EventStore eventStore = new MockEventStore(new PersistenceManagerProvider() {});

        assertNotNull(eventStore);

        return eventStore;
    }
}