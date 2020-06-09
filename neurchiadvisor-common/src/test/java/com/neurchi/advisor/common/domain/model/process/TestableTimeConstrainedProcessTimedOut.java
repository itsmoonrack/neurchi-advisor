package com.neurchi.advisor.common.domain.model.process;

public class TestableTimeConstrainedProcessTimedOut extends ProcessTimedOut {

    public TestableTimeConstrainedProcessTimedOut(
            final String tenantId,
            final ProcessId processId,
            final int totalRetriesPermitted,
            final int retryCount) {

        super(tenantId, processId, totalRetriesPermitted, retryCount);
    }

    public TestableTimeConstrainedProcessTimedOut(
            final String tenantId,
            final ProcessId processId) {

        super(tenantId, processId, 0, 0);
    }
}
