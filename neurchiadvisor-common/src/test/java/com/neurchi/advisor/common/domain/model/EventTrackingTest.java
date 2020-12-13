package com.neurchi.advisor.common.domain.model;

import com.neurchi.advisor.common.notification.NotificationReader;
import com.neurchi.advisor.common.port.adapter.messaging.rabbitmq.ExchangeListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.neurchi.advisor.common.port.adapter.messaging.Exchanges.*;

public abstract class EventTrackingTest {

    protected TestAdvisoryRabbitMQExchangeListener advisoryRabbitMQExchangeListener;
    protected TestSubscriptionRabbitMQExchangeListener subscriptionRabbitMQExchangeListener;
    protected TestIdentityAccessRabbitMQExchangeListener identityAccessRabbitMQExchangeListener;

    private List<Class<? extends DomainEvent>> handledEvents;
    private Map<String, String> handledNotifications;

    protected void expectedEvent(final Class<? extends DomainEvent> domainEventType) {
        this.expectedEvent(domainEventType, 1);
    }

    protected void expectedEvent(final Class<? extends DomainEvent> domainEventType, final int total) {
        final long count = this.handledEvents.stream()
                .filter(domainEventType::equals)
                .count();

        if (count != total) {
            throw new IllegalStateException("Expected " + total + " " + domainEventType.getSimpleName() + " events, but handled "
                    + this.handledEvents.size() + " events: " + this.handledEvents);
        }
    }

    protected void expectedEvents(final int eventCount) {
        if (this.handledEvents.size() != eventCount) {
            throw new IllegalStateException("Expected " + eventCount + " events, but handled " + this.handledEvents.size()
                    + " events: " + this.handledEvents);
        }
    }

    protected void expectedNotification(final Class<? extends DomainEvent> notificationType) {
        this.expectedNotification(notificationType, 1);
    }

    protected void expectedNotification(final Class<? extends DomainEvent> notificationType, final int total) {
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
            // ignore
        }

        final String notificationTypeName = notificationType.getName();

        final long count = this.handledNotifications.values().stream()
                .filter(notificationTypeName::equals)
                .count();

        if (count != total) {
            throw new IllegalStateException("Expected " + total + " " + notificationType.getSimpleName()
                    + " notifications, but handled " + this.handledNotifications.size() + " notifications: "
                    + this.handledNotifications.values());
        }
    }

    protected void expectedNotifications(final int notificationCount) {
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
            // ignore
        }

        if (this.handledNotifications.size() != notificationCount) {
            throw new IllegalStateException("Expected " + notificationCount + " notifications, but handled "
                    + this.handledNotifications.size() + " notifications: " + this.handledNotifications.values());
        }
    }

    @BeforeEach
    protected void setUp() throws Exception {
        DomainEventPublisher.instance().reset();

        DomainEventPublisher.instance().subscribe(new DomainEventSubscriber<DomainEvent>() {
            @Override
            public void handleEvent(final DomainEvent domainEvent) {
                handledEvents.add(domainEvent.getClass());
            }

            @Override
            public Class<DomainEvent> subscribedToType() {
                return DomainEvent.class;
            }
        });

        this.handledEvents = new ArrayList<>();
        this.handledNotifications = new HashMap<>();

        this.advisoryRabbitMQExchangeListener = new TestAdvisoryRabbitMQExchangeListener();
        this.subscriptionRabbitMQExchangeListener = new TestSubscriptionRabbitMQExchangeListener();
        this.identityAccessRabbitMQExchangeListener = new TestIdentityAccessRabbitMQExchangeListener();

        clearExchangeListeners();

        Thread.sleep(200L);
    }

    private void clearExchangeListeners() throws InterruptedException {
        // At beginning of the test, give MQExchangeListeners time to receive
        // messages from queues which were published by previous tests.
        // Since RabbitMQ Java Client does not allow queue listing or cleaning
        // all queues at once, we can just consume all messages and do
        // nothing with them as a work-around.
        Thread.sleep(500L);

        this.advisoryRabbitMQExchangeListener.clear();
        this.subscriptionRabbitMQExchangeListener.clear();
        this.identityAccessRabbitMQExchangeListener.clear();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.advisoryRabbitMQExchangeListener.close();
        this.subscriptionRabbitMQExchangeListener.close();
        this.identityAccessRabbitMQExchangeListener.close();
    }

    private abstract class TestExchangeListener extends ExchangeListener {

        public void clear() {
            handledEvents.clear();
            handledNotifications.clear();
        }

        @Override
        protected String[] listensTo() {
            return null; // receive all
        }

        @Override
        protected void filteredDispatch(final String type, final String textMessage) {
            synchronized (handledNotifications) {
                NotificationReader notification = new NotificationReader(textMessage);
                handledNotifications.put(notification.notificationIdAsString(), type);
            }
        }
    }

    protected class TestAdvisoryRabbitMQExchangeListener extends TestExchangeListener {
        @Override
        protected String exchangeName() {
            return AdvisoryExchangeName;
        }
    }

    protected class TestSubscriptionRabbitMQExchangeListener extends TestExchangeListener {
        @Override
        protected String exchangeName() {
            return SubscriptionExchangeName;
        }
    }

    protected class TestIdentityAccessRabbitMQExchangeListener extends TestExchangeListener {
        @Override
        protected String exchangeName() {
            return IdentityAccessExchangeName;
        }
    }
}
