package com.neurchi.advisor.common.port.adapter.persistence.hibernate;

import com.neurchi.advisor.common.notification.Notification;
import com.neurchi.advisor.common.notification.PublishedNotificationTracker;
import com.neurchi.advisor.common.notification.PublishedNotificationTrackerStore;
import com.neurchi.advisor.common.persistence.PersistenceManagerProvider;

import java.util.List;

public class HibernatePublishedNotificationTrackerStore
        extends AbstractHibernateSession
        implements PublishedNotificationTrackerStore {

    private String typeName;

    HibernatePublishedNotificationTrackerStore(
            final PersistenceManagerProvider persistenceManagerProvider,
            final String typeName) {

        if (!persistenceManagerProvider.hasHibernateSession()) {
            throw new IllegalArgumentException("The PersistenceManagerProvider must have a Hibernate Session.");
        }

        this.setSession(persistenceManagerProvider.hibernateSession());
        this.setTypeName(typeName);
    }

    HibernatePublishedNotificationTrackerStore() {
        super();
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
        return null;
    }

    private void setTypeName(final String typeName) {
        this.typeName = typeName;
    }
}
