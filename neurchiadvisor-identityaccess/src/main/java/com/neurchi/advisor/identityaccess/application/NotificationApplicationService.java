package com.neurchi.advisor.identityaccess.application;

import com.neurchi.advisor.common.event.EventStore;
import com.neurchi.advisor.common.notification.NotificationLog;
import com.neurchi.advisor.common.notification.NotificationLogFactory;
import com.neurchi.advisor.common.notification.NotificationLogId;
import com.neurchi.advisor.common.notification.NotificationPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationApplicationService {

    private final EventStore eventStore;
    private final NotificationPublisher notificationPublisher;

    NotificationApplicationService(final EventStore eventStore, final NotificationPublisher notificationPublisher) {
        this.eventStore = eventStore;
        this.notificationPublisher = notificationPublisher;
    }

    @Transactional(readOnly = true)
    public NotificationLog currentNotificationLog() {
        NotificationLogFactory factory = new NotificationLogFactory(this.eventStore());

        return factory.createCurrentNotificationLog();
    }

    @Transactional(readOnly = true)
    public NotificationLog notificationLog(final String notificationLogId) {
        NotificationLogFactory factory = new NotificationLogFactory(this.eventStore());

        return factory.createNotificationLog(new NotificationLogId(notificationLogId));
    }

    @Transactional
    public void publishNotifications() {
        this.notificationPublisher().publishNotifications();
    }

    protected EventStore eventStore() {
        return this.eventStore;
    }

    protected NotificationPublisher notificationPublisher() {
        return this.notificationPublisher;
    }
}
