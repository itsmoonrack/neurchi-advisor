package com.neurchi.advisor.common.notification;

import java.util.Collections;
import java.util.List;

public final class NotificationLog {

    private boolean archived;
    private List<Notification> notifications;
    private String notificationLogId;
    private String nextNotificationLogId;
    private String previousNotificationLogId;

    NotificationLog(
            final String notificationLogId,
            final String nextNotificationLogId,
            final String previousNotificationLogId,
            final List<Notification> notifications,
            final boolean archivedIndicator) {

        this.archived = archivedIndicator;
        this.notifications = notifications;
        this.notificationLogId = notificationLogId;
        this.nextNotificationLogId = nextNotificationLogId;
        this.previousNotificationLogId = previousNotificationLogId;
    }

    public boolean isArchived() {
        return this.archived;
    }

    public List<Notification> notifications() {
        return Collections.unmodifiableList(this.notifications);
    }

    public NotificationLogId decodedNotificationLogId() {
        return new NotificationLogId(this.notificationLogId());
    }

    public String notificationLogId() {
        return this.notificationLogId;
    }

    public NotificationLogId decodedNextNotificationLogId() {
        return new NotificationLogId(this.nextNotificationLogId());
    }

    public String nextNotificationLogId() {
        return this.nextNotificationLogId;
    }

    public boolean hasNextNotificationLog() {
        return this.nextNotificationLogId() != null;
    }

    public NotificationLogId decodedPreviousNotificationLogId() {
        return new NotificationLogId(this.previousNotificationLogId());
    }

    public String previousNotificationLogId() {
        return this.previousNotificationLogId;
    }

    public boolean hasPreviousNotificationLog() {
        return this.previousNotificationLogId() != null;
    }

    public int totalNotifications() {
        return this.notifications.size();
    }
}
