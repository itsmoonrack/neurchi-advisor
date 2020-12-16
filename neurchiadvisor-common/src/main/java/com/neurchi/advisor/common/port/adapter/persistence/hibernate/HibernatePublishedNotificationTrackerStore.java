package com.neurchi.advisor.common.port.adapter.persistence.hibernate;

import com.neurchi.advisor.common.notification.Notification;
import com.neurchi.advisor.common.notification.PublishedNotificationTracker;
import com.neurchi.advisor.common.notification.PublishedNotificationTrackerStore;

import java.util.List;

public class HibernatePublishedNotificationTrackerStore
        extends AbstractHibernateSession
        implements PublishedNotificationTrackerStore {

    private String typeName;

    HibernatePublishedNotificationTrackerStore(final String typeName) {
        this.setTypeName(typeName);
    }

    HibernatePublishedNotificationTrackerStore() {

    }

    @Override
    public PublishedNotificationTracker publishedNotificationTracker() {
        return null;
    }

    @Override
    public PublishedNotificationTracker publishedNotificationTracker(final String typeName) {
        return null;
    }

    @Override
    public void trackMostRecentPublishedNotification(final PublishedNotificationTracker publishedNotificationTracker, final List<Notification> notifications) {

    }

    @Override
    public String typeName() {
        return this.typeName;
    }

    private void setTypeName(final String typeName) {
        this.typeName = typeName;
    }
}
