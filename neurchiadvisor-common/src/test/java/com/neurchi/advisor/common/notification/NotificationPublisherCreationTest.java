package com.neurchi.advisor.common.notification;

import com.neurchi.advisor.common.CommonTestCase;
import com.neurchi.advisor.common.event.EventStore;
import com.neurchi.advisor.common.event.MockEventStore;
import com.neurchi.advisor.common.persistence.PersistenceManagerProvider;
import com.neurchi.advisor.common.port.adapter.notification.RabbitMQNotificationPublisher;
import com.neurchi.advisor.common.port.adapter.persistence.hibernate.HibernatePublishedNotificationTrackerStore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class NotificationPublisherCreationTest extends CommonTestCase {

    @Test
    public void TestNewNotificationPublisher() {

        EventStore eventStore = new MockEventStore(new PersistenceManagerProvider() {});

        assertNotNull(eventStore);

        PublishedNotificationTrackerStore publishedNotificationTrackerStore =
                new HibernatePublishedNotificationTrackerStore(
                        new PersistenceManagerProvider(this.session()),
                        "unit.test");

        NotificationPublisher notificationPublisher =
                new RabbitMQNotificationPublisher(
                        eventStore,
                        publishedNotificationTrackerStore,
                        "unit.test");

        assertNotNull(notificationPublisher);
    }

}
