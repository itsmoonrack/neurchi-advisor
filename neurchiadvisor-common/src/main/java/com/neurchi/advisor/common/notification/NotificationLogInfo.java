package com.neurchi.advisor.common.notification;

class NotificationLogInfo {

    private NotificationLogId notificationLogId;
    private long totalLogged;

    NotificationLogInfo(final NotificationLogId notificationLogId, final long totalLogged) {
        this.notificationLogId = notificationLogId;
        this.totalLogged = totalLogged;
    }

    public NotificationLogId notificationLogId() {
        return this.notificationLogId;
    }

    public long totalLogged() {
        return this.totalLogged;
    }
}
