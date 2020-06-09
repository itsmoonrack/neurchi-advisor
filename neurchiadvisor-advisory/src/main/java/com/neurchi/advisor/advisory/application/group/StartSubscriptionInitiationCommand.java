package com.neurchi.advisor.advisory.application.group;

public final class StartSubscriptionInitiationCommand {

    private final String tenantId;
    private final String groupId;

    public StartSubscriptionInitiationCommand(final String tenantId, final String groupId) {
        this.tenantId = tenantId;
        this.groupId = groupId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getGroupId() {
        return groupId;
    }
}
