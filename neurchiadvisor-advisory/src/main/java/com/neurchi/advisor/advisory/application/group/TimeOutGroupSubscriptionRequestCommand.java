package com.neurchi.advisor.advisory.application.group;

import java.time.Instant;

public final class TimeOutGroupSubscriptionRequestCommand {

    private final String tenantId;
    private final String processId;
    private final Instant timeOutDate;

    public TimeOutGroupSubscriptionRequestCommand(final String tenantId, final String processId, final Instant timeOutDate) {
        this.tenantId = tenantId;
        this.processId = processId;
        this.timeOutDate = timeOutDate;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getProcessId() {
        return processId;
    }

    public Instant getTimeOutDate() {
        return this.timeOutDate;
    }
}
