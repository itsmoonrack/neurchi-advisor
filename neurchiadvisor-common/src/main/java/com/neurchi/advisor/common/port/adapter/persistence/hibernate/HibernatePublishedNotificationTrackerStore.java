package com.neurchi.advisor.common.port.adapter.persistence.hibernate;

import com.neurchi.advisor.common.notification.Notification;
import com.neurchi.advisor.common.notification.PublishedNotificationTracker;
import com.neurchi.advisor.common.notification.PublishedNotificationTrackerStore;
import org.hibernate.NaturalIdLoadAccess;

import java.util.List;

public class HibernatePublishedNotificationTrackerStore
        extends AbstractHibernateSession
        implements PublishedNotificationTrackerStore {

    private String typeName;

    public HibernatePublishedNotificationTrackerStore(final String typeName) {
        this.setTypeName(typeName);
    }

    public HibernatePublishedNotificationTrackerStore() {

    }

    @Override
    public PublishedNotificationTracker publishedNotificationTracker() {
        return this.publishedNotificationTracker(this.typeName());
    }

    @Override
    public PublishedNotificationTracker publishedNotificationTracker(final String typeName) {
        NaturalIdLoadAccess<PublishedNotificationTracker> query = this.session()
                .byNaturalId(PublishedNotificationTracker.class)
                .using("typeName", typeName);

        return query.loadOptional().orElse(new PublishedNotificationTracker(typeName));
    }

    @Override
    public void trackMostRecentPublishedNotification(final PublishedNotificationTracker publishedNotificationTracker, final List<Notification> notifications) {
        int lastIndex = notifications.size() - 1;

        if (lastIndex >= 0) {
            long mostRecentId = notifications.get(lastIndex).notificationId();

            publishedNotificationTracker.setMostRecentPublishedNotificationId(mostRecentId);

            this.session().save(publishedNotificationTracker);
        }
    }

    @Override
    public String typeName() {
        return this.typeName;
    }

    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }
}
