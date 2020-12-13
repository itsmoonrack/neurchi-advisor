package com.neurchi.advisor.advisory.application.group;

public final class RetryGroupSubscriptionRequestCommand {

    private final String tenantId;
    private final String processId;

    public RetryGroupSubscriptionRequestCommand(final String tenantId, final String processId) {
        this.tenantId = tenantId;
        this.processId = processId;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public String getProcessId() {
        return this.processId;
    }
}
