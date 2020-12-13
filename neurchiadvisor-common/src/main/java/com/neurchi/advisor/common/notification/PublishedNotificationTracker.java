package com.neurchi.advisor.common.notification;

import com.neurchi.advisor.common.AssertionConcern;

import java.io.Serializable;
import java.util.Objects;

public class PublishedNotificationTracker extends AssertionConcern implements Serializable {

    private int concurrencyVersion;
    private long mostRecentPublishedNotificationId;
    private long publishedNotificationTrackerId;
    private String typeName;

    public PublishedNotificationTracker(final String typeName) {
        this.setTypeName(typeName);
    }

    public long mostRecentPublishedNotificationId() {
        return this.mostRecentPublishedNotificationId;
    }

    public void setMostRecentPublishedNotificationId(final long mostRecentPublishedNotificationId) {
        this.mostRecentPublishedNotificationId = mostRecentPublishedNotificationId;
    }

    public long publishedNotificationTrackerId() {
        return this.publishedNotificationTrackerId;
    }

    public String typeName() {
        return this.typeName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PublishedNotificationTracker that = (PublishedNotificationTracker) o;
        return mostRecentPublishedNotificationId == that.mostRecentPublishedNotificationId && publishedNotificationTrackerId == that.publishedNotificationTrackerId && typeName.equals(that.typeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mostRecentPublishedNotificationId, publishedNotificationTrackerId, typeName);
    }

    protected PublishedNotificationTracker() {
        super();
    }

    protected int concurrencyVersion() {
        return this.concurrencyVersion;
    }

    protected void setConcurrencyVersion(final int concurrencyVersion) {
        this.concurrencyVersion = concurrencyVersion;
    }

    protected void setPublishedNotificationTrackerId(final long publishedNotificationTrackerId) {
        this.publishedNotificationTrackerId = publishedNotificationTrackerId;
    }

    protected void setTypeName(final String typeName) {
        this.assertArgumentNotEmpty(typeName, "The tracker type name must be provided.");
        this.assertArgumentLength(typeName, 100, "The tracker type name must be 100 characters or less.");

        this.typeName = typeName;
    }
}
