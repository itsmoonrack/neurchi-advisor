package com.neurchi.advisor.common.notification;

import java.util.List;

public interface PublishedNotificationTrackerStore {

    PublishedNotificationTracker publishedNotificationTracker();

    PublishedNotificationTracker publishedNotificationTracker(String typeName);

    void trackMostRecentPublishedNotification(
            PublishedNotificationTracker publishedNotificationTracker,
            List<Notification> notifications);

    String typeName();
}
