package com.neurchi.advisor.group.domain.model.group;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.group.domain.model.collaborator.Participant;
import com.neurchi.advisor.group.domain.model.tenant.Tenant;

import java.time.LocalDateTime;

public final class GroupSubscribed implements DomainEvent {

    private Participant creator;
    private String description;
    private int eventVersion;
    private GroupId groupId;
    private CoverPhoto cover;
    private LocalDateTime occurredOn;
    private String name;
    private Tenant tenant;

    GroupSubscribed(
            final Participant creator,
            final String description,
            final GroupId groupId,
            final CoverPhoto cover,
            final String name,
            final Tenant tenant) {

        this.creator = creator;
        this.description = description;
        this.eventVersion = 1;
        this.groupId = groupId;
        this.cover = cover;
        this.occurredOn = LocalDateTime.now();
        this.name = name;
        this.tenant = tenant;
    }

    @Override
    public int eventVersion() {
        return this.eventVersion;
    }

    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }

    public Participant creator() {
        return creator;
    }

    public String description() {
        return description;
    }

    public GroupId groupId() {
        return groupId;
    }

    public CoverPhoto cover() {
        return cover;
    }

    public String name() {
        return name;
    }

    public Tenant tenant() {
        return tenant;
    }
}
