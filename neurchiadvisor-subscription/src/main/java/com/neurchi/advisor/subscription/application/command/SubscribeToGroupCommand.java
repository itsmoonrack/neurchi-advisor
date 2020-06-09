package com.neurchi.advisor.subscription.application.command;

import java.time.Instant;

public final class SubscribeToGroupCommand {

    private String tenantId;
    private String groupId;
    private String identity;
    private boolean administrator;
    private String name;
    private String description;
    private Integer memberCount;
    private Instant createdOn;
    private String coverId;
    private Integer offsetX;
    private Integer offsetY;
    private String coverSource;

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(final Integer memberCount) {
        this.memberCount = memberCount;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(final Instant createdOn) {
        this.createdOn = createdOn;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(final String identity) {
        this.identity = identity;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public void setAdministrator(final boolean administrator) {
        this.administrator = administrator;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getCoverId() {
        return coverId;
    }

    public void setCoverId(final String coverId) {
        this.coverId = coverId;
    }

    public Integer getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(final Integer offsetX) {
        this.offsetX = offsetX;
    }

    public Integer getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(final Integer offsetY) {
        this.offsetY = offsetY;
    }

    public String getCoverSource() {
        return coverSource;
    }

    public void setCoverSource(final String source) {
        this.coverSource = source;
    }
}
