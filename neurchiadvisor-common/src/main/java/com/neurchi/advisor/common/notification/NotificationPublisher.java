package com.neurchi.advisor.common.notification;

public interface NotificationPublisher {

    void publishNotifications();

    boolean internalOnlyTestConfirmation();
}
