package com.neurchi.advisor.advisory.application.group;

public final class InitiateSubscriptionCommand {

    private final String tenantId;
    private final String groupId;
    private final String subscriptionId;

    public InitiateSubscriptionCommand(final String tenantId, final String groupId, final String subscriptionId) {
        this.tenantId = tenantId;
        this.groupId = groupId;
        this.subscriptionId = subscriptionId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }
}
