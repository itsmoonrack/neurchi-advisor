package com.neurchi.advisor.advisory.application.group;

public final class RequestGroupSubscriptionCommand {

    private final String tenantId;
    private final String groupId;

    public RequestGroupSubscriptionCommand(final String tenantId, final String groupId) {
        this.tenantId = tenantId;
        this.groupId = groupId;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public String getGroupId() {
        return this.groupId;
    }
}
