package com.neurchi.advisor.common.domain.model.process;

import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.time.Instant;

public class ProcessTimedOut implements DomainEvent {

    private int eventVersion;
    private Instant occurredOn;
    private ProcessId processId;
    private int retryCount;
    private String tenantId;
    private int totalRetriesPermitted;

    public ProcessTimedOut(
            final String tenantId,
            final ProcessId processId,
            final int totalRetriesPermitted,
            final int retryCount) {

        this.eventVersion = 1;
        this.occurredOn = Instant.now();
        this.processId = processId;
        this.retryCount = retryCount;
        this.tenantId = tenantId;
        this.totalRetriesPermitted = totalRetriesPermitted;
    }

    @Override
    public int eventVersion() {
        return this.eventVersion;
    }

    @Override
    public Instant occurredOn() {
        return this.occurredOn;
    }

    public ProcessId processId() {
        return this.processId;
    }

    public int retryCount() {
        return this.retryCount;
    }

    public String tenantId() {
        return this.tenantId;
    }

    public int totalRetriesPermitted() {
        return this.totalRetriesPermitted;
    }
}
