package com.neurchi.advisor.group.domain.model.group;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.group.domain.model.tenant.Tenant;

import java.time.LocalDateTime;

public class GroupDescriptionChanged implements DomainEvent {

    private Tenant tenant;
    private GroupId groupId;
    private String description;
    private int eventVersion;
    private LocalDateTime occurredOn;

    GroupDescriptionChanged(
            final Tenant tenant,
            final GroupId groupId,
            final String description) {

        this.description = description;
        this.eventVersion = 1;
        this.groupId = groupId;
        this.occurredOn = LocalDateTime.now();
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

    public String description() {
        return description;
    }

    public GroupId groupId() {
        return groupId;
    }

    public Tenant tenant() {
        return tenant;
    }
}
