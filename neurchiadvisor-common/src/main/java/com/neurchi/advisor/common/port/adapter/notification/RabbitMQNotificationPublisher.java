package com.neurchi.advisor.common.port.adapter.notification;

import com.neurchi.advisor.common.event.EventStore;
import com.neurchi.advisor.common.event.StoredEvent;
import com.neurchi.advisor.common.notification.*;
import com.neurchi.advisor.common.port.adapter.messaging.rabbitmq.ConnectionSettings;
import com.neurchi.advisor.common.port.adapter.messaging.rabbitmq.Exchange;
import com.neurchi.advisor.common.port.adapter.messaging.rabbitmq.MessageParameters;
import com.neurchi.advisor.common.port.adapter.messaging.rabbitmq.MessageProducer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RabbitMQNotificationPublisher implements NotificationPublisher {

    private final EventStore eventStore;
    private final String exchangeName;
    private final PublishedNotificationTrackerStore publishedNotificationTrackerStore;

    RabbitMQNotificationPublisher(
            final EventStore eventStore,
            final PublishedNotificationTrackerStore publishedNotificationTrackerStore,
            final Object messagingLocator) {

        this.eventStore = eventStore;
        this.exchangeName = (String) messagingLocator;
        this.publishedNotificationTrackerStore = publishedNotificationTrackerStore;
    }

    @Override
    public void publishNotifications() {
        PublishedNotificationTracker publishedNotificationTracker =
                this.publishedNotificationTrackerStore().publishedNotificationTracker();

        List<Notification> notifications =
                this.unpublishedNotifications(
                        publishedNotificationTracker.mostRecentPublishedNotificationId())
                .collect(Collectors.toList());

        try (MessageProducer messageProducer = this.messageProducer()) {
            notifications.forEach(notification -> this.publish(notification, messageProducer));

            this.publishedNotificationTrackerStore()
                    .trackMostRecentPublishedNotification(
                            publishedNotificationTracker,
                            notifications);
        }
    }

    @Override
    public boolean internalOnlyTestConfirmation() {
        throw new UnsupportedOperationException("Not supported by production implementation.");
    }

    private EventStore eventStore() {
        return this.eventStore;
    }

    private String exchangeName() {
        return this.exchangeName;
    }

    private Stream<Notification> unpublishedNotifications(final long mostRecentPublishedMessageId) {
        return this.eventStore().allStoredEventsSince(mostRecentPublishedMessageId).map(this::notificationFrom);
    }

    private MessageProducer messageProducer() {

        // creates exchange if non-existing
        Exchange exchange =
                Exchange.fanOutInstance(
                        ConnectionSettings.instance(),
                        this.exchangeName(),
                        true);

        return MessageProducer.instance(exchange);
    }

    private Notification notificationFrom(final StoredEvent storedEvent) {
        return new Notification(storedEvent.eventId(), storedEvent.toDomainEvent());
    }

    private void publish(
            final Notification notification,
            final MessageProducer messageProducer) {

        MessageParameters messageParameters =
                MessageParameters.durableTextParameters(
                        notification.typeName(),
                        Long.toString(notification.notificationId()),
                        notification.occurredOn());

        String textMessage =
                NotificationSerializer
                .instance()
                .serialize(notification);

        messageProducer.send(textMessage, messageParameters);
    }

    private PublishedNotificationTrackerStore publishedNotificationTrackerStore() {
        return this.publishedNotificationTrackerStore;
    }
}
