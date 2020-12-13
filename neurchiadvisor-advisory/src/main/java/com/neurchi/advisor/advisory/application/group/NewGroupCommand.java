package com.neurchi.advisor.advisory.application.group;

import java.time.LocalDateTime;

public final class NewGroupCommand {

    private final String tenantId;
    private final String groupId;
    private final String groupOwnerId;
    private final String name;
    private final String description;
    private final Integer memberCount;
    private final LocalDateTime createdOn;
    private final String cover;

    public NewGroupCommand(
            final String tenantId,
            final String groupId,
            final String groupOwnerId,
            final String name,
            final String description,
            final Integer memberCount,
            final LocalDateTime createdOn,
            final String cover) {

        this.tenantId = tenantId;
        this.groupId = groupId;
        this.groupOwnerId = groupOwnerId;
        this.name = name;
        this.description = description;
        this.memberCount = memberCount;
        this.createdOn = createdOn;
        this.cover = cover;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupOwnerId() {
        return groupOwnerId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public String getCover() {
        return cover;
    }
}
