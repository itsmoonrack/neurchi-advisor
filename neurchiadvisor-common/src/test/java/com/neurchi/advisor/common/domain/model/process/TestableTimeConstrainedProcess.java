package com.neurchi.advisor.common.domain.model.process;

import java.time.Duration;

public class TestableTimeConstrainedProcess extends AbstractProcess {

    private boolean confirm1;
    private boolean confirm2;

    public TestableTimeConstrainedProcess(
            final String tenantId,
            final ProcessId processId,
            final String description,
            final Duration allowableDuration) {

        super(tenantId, processId, description, allowableDuration);
    }

    public TestableTimeConstrainedProcess(
            final String tenantId,
            final ProcessId processId,
            final String description,
            final Duration allowableDuration,
            final int totalRetriesPermitted) {

        super(tenantId, processId, description, allowableDuration, totalRetriesPermitted);
    }

    public void confirm1() {
        this.confirm1 = true;

        this.completeProcess(ProcessCompletionType.NotCompleted);
    }

    public void confirm2() {
        this.confirm2 = true;

        this.completeProcess(ProcessCompletionType.CompletedNormally);
    }

    protected TestableTimeConstrainedProcess() {
        super();
    }

    @Override
    protected boolean completenessVerified() {
        return this.confirm1 && this.confirm2;
    }

    @Override
    protected Class<? extends ProcessTimedOut> processTimedOutEventType() {
        return TestableTimeConstrainedProcessTimedOut.class;
    }
}
