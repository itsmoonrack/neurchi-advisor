package com.neurchi.advisor.identityaccess.application;

import com.neurchi.advisor.common.event.EventStore;
import com.neurchi.advisor.common.event.TestableDomainEvent;
import com.neurchi.advisor.common.notification.NotificationLog;
import com.neurchi.advisor.common.notification.NotificationLogFactory;
import com.neurchi.advisor.common.notification.NotificationLogId;
import com.neurchi.advisor.common.notification.NotificationPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationApplicationServiceTest extends ApplicationServiceTest {

    private EventStore eventStore;
    private NotificationApplicationService notificationApplicationService;
    private NotificationPublisher notificationPublisher;

    @Test
    public void TestCurrentNotificationLog() {
        NotificationLog log =
                this.notificationApplicationService.currentNotificationLog();

        assertTrue(NotificationLogFactory.notificationsPerLog() >= log.totalNotifications());
        assertTrue(eventStore.countStoredEvents() >= log.totalNotifications());
        assertFalse(log.hasNextNotificationLog());
        assertTrue(log.hasPreviousNotificationLog());
        assertFalse(log.isArchived());
    }

    @Test
    public void TestNotificationLog() {
        NotificationLogId id = NotificationLogId.first(NotificationLogFactory.notificationsPerLog());

        NotificationLog log = this.notificationApplicationService.notificationLog(id.encoded());

        assertEquals(NotificationLogFactory.notificationsPerLog(), log.totalNotifications());
        assertTrue(eventStore.countStoredEvents() >= log.totalNotifications());
        assertTrue(log.hasNextNotificationLog());
        assertFalse(log.hasPreviousNotificationLog());
        assertTrue(log.isArchived());
    }

    @Test
    public void TestPublishNotifications() {
        this.notificationApplicationService.publishNotifications();

        assertTrue(this.notificationPublisher.internalOnlyTestConfirmation());
    }

    @BeforeEach
    protected void setUp() {

        super.setUp();

        this.notificationApplicationService = this.notificationApplicationService();

        this.eventStore = notificationApplicationService.eventStore();

        this.notificationPublisher = notificationApplicationService.notificationPublisher();

        for (int index = 1; index <= 31; ++index) {
            this.eventStore.append(new TestableDomainEvent(index, "Event: " + index));
        }
    }
}
