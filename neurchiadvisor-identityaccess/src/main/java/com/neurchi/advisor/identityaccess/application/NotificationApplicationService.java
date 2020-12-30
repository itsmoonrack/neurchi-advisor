package com.neurchi.advisor.identityaccess.application;

import com.neurchi.advisor.common.event.EventStore;
import com.neurchi.advisor.common.notification.NotificationLog;
import com.neurchi.advisor.common.notification.NotificationLogFactory;
import com.neurchi.advisor.common.notification.NotificationLogId;
import com.neurchi.advisor.common.notification.NotificationPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

public class NotificationApplicationService {

    @Autowired
    private EventStore eventStore;
    @Autowired
    private NotificationPublisher notificationPublisher;

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
    @Scheduled(fixedDelay = 1000)
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
