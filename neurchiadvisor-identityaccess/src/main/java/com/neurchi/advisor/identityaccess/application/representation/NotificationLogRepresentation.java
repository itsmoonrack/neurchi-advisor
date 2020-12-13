package com.neurchi.advisor.identityaccess.application.representation;

import com.neurchi.advisor.common.notification.Notification;
import com.neurchi.advisor.common.notification.NotificationLog;

import java.util.Collection;

public class NotificationLogRepresentation {

    private boolean archived;
    private String id;
    private Collection<Notification> notifications;

    public NotificationLogRepresentation(final NotificationLog log) {
        this.archived = log.isArchived();
        this.id = log.notificationLogId();
        this.notifications = log.notifications();
    }

    public boolean isArchived() {
        return archived;
    }

    public String getId() {
        return id;
    }

    public Collection<Notification> getNotifications() {
        return notifications;
    }
}
