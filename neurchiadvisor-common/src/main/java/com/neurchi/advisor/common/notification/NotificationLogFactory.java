package com.neurchi.advisor.common.notification;

import com.neurchi.advisor.common.event.EventStore;
import com.neurchi.advisor.common.event.StoredEvent;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class NotificationLogFactory {

    // this could be a configuration
    private static final int NOTIFICATIONS_PER_LOG = 20;

    private final EventStore eventStore;

    public static int notificationsPerLog() {
        return NOTIFICATIONS_PER_LOG;
    }

    public NotificationLogFactory(final EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public NotificationLog createCurrentNotificationLog() {
        return this.createNotificationLog(
                this.calculateCurrentNotificationLogId(eventStore));
    }


    public NotificationLog createNotificationLog(final NotificationLogId notificationLogId) {

        long count = this.eventStore().countStoredEvents();

        NotificationLogInfo info = new NotificationLogInfo(notificationLogId, count);

        return this.createNotificationLog(info);
    }

    private NotificationLogInfo calculateCurrentNotificationLogId(final EventStore eventStore) {

        long count = eventStore.countStoredEvents();

        long remainder = count % NOTIFICATIONS_PER_LOG;

        if (remainder == 0 && count > 0) {
            remainder = NOTIFICATIONS_PER_LOG;
        }

        long low = count - remainder + 1;

        // ensures a minted id value even though there may
        // not be a full set of notifications at present
        long high = low + NOTIFICATIONS_PER_LOG - 1;

        return new NotificationLogInfo(new NotificationLogId(low, high), count);
    }

    private NotificationLog createNotificationLog(final NotificationLogInfo notificationLogInfo) {

        Stream<StoredEvent> storedEvents =
                this.eventStore().allStoredEventsBetween(
                        notificationLogInfo.notificationLogId().low(),
                        notificationLogInfo.notificationLogId().high());

        boolean archivedIndicator =
                notificationLogInfo.notificationLogId().high() < notificationLogInfo.totalLogged();

        NotificationLogId next = archivedIndicator ?
                notificationLogInfo.notificationLogId().next(NOTIFICATIONS_PER_LOG) :
                null;

        NotificationLogId previous =
                notificationLogInfo.notificationLogId().previous(NOTIFICATIONS_PER_LOG);

        return new NotificationLog(
                notificationLogInfo.notificationLogId().encoded(),
                NotificationLogId.encoded(next),
                NotificationLogId.encoded(previous),
                this.notificationsFrom(storedEvents),
                archivedIndicator);
    }

    private List<Notification> notificationsFrom(final Stream<StoredEvent> storedEvents) {
        return storedEvents.map(storedEvent -> new Notification(storedEvent.eventId(), storedEvent.toDomainEvent())).collect(Collectors.toList());
    }

    private EventStore eventStore() {
        return this.eventStore;
    }
}
