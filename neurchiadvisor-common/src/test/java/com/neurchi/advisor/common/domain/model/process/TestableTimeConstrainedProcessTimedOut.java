package com.neurchi.advisor.common.domain.model.process;

public class TestableTimeConstrainedProcessTimedOut extends ProcessTimedOut {

    TestableTimeConstrainedProcessTimedOut(
            final String tenantId,
            final ProcessId processId,
            final int totalRetriesPermitted,
            final int retryCount) {

        super(tenantId, processId, totalRetriesPermitted, retryCount);
    }

    TestableTimeConstrainedProcessTimedOut(
            final String tenantId,
            final ProcessId processId) {

        super(tenantId, processId, 0, 0);
    }
}
