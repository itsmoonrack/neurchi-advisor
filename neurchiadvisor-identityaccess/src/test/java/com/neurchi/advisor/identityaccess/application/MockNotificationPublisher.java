package com.neurchi.advisor.identityaccess.application;

import com.neurchi.advisor.common.notification.NotificationPublisher;

public class MockNotificationPublisher implements NotificationPublisher {

    private boolean confirmed;

    @Override
    public void publishNotifications() {
        this.confirmed = true;
    }

    @Override
    public boolean internalOnlyTestConfirmation() {
        return this.confirmed;
    }
}
