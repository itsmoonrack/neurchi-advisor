package com.neurchi.advisor.identityaccess.application.representation;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.neurchi.advisor.common.notification.Notification;
import com.neurchi.advisor.common.notification.NotificationLog;

import java.util.Collection;

@JacksonXmlRootElement(localName = "notifications")
public class NotificationLogRepresentation {

    @JacksonXmlProperty(isAttribute = true)
    private final boolean archived;
    @JacksonXmlProperty(isAttribute = true)
    private final String id;
    private final Collection<Notification> notifications;

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
