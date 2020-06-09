package com.neurchi.advisor.common.domain.model.process;

import com.neurchi.advisor.common.AssertionConcern;
import com.neurchi.advisor.common.domain.model.DomainEventPublisher;

import java.lang.reflect.Constructor;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class TimeConstrainedProcessTracker extends AssertionConcern {

    private Duration allowableDuration;
    private boolean completed;
    private int concurrencyVersion;
    private String description;
    private ProcessId processId;
    private boolean processInformedOfTimeout;
    private String processTimedOutEventType;
    private int retryCount;
    private String tenantId;
    private long timeConstrainedProcessTrackerId;
    private Instant timeoutOccursOn;
    private int totalRetriesPermitted;

    public TimeConstrainedProcessTracker(
            final String tenantId,
            final ProcessId processId,
            final String description,
            final Instant originalStartTime,
            final Duration allowableDuration,
            final int totalRetriesPermitted,
            final String processTimedOutEventType) {

        this.setAllowableDuration(allowableDuration);
        this.setDescription(description);
        this.setProcessId(processId);
        this.setProcessTimedOutEventType(processTimedOutEventType);
        this.setTenantId(tenantId);
        this.setTimeConstrainedProcessTrackerId(-1L);
        this.setTimeoutOccursOn(originalStartTime.plus(allowableDuration));
        this.setTotalRetriesPermitted(totalRetriesPermitted);
    }

    public Duration allowableDuration() {
        return this.allowableDuration;
    }

    public void complete() {
        this.completed = true;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public String description() {
        return this.description;
    }

    public void failWhenConcurrencyViolation(final int version) {
        this.assertStateTrue(
                version == this.concurrencyVersion(),
                "Concurrency Violation: Stale data detected. Entity was already modified.");
    }

    public ProcessId processId() {
        return this.processId;
    }

    public boolean isProcessInformedOfTimeout() {
        return this.processInformedOfTimeout;
    }

    public String processTimedOutEventType() {
        return this.processTimedOutEventType;
    }

    public boolean hasTimedOut() {
        final Instant timeout = this.timeoutOccursOn();
        final Instant now = Instant.now();

        return timeout.equals(now) || timeout.isBefore(now);
    }

    public void informProcessTimedOut() {
        if (!this.isProcessInformedOfTimeout() && this.hasTimedOut()) {

            final ProcessTimedOut processTimedOut;

            if (this.totalRetriesPermitted() == 0) {
                processTimedOut = this.processTimedOutEvent();

                this.setProcessInformedOfTimeout(true);
            } else {
                this.incrementRetryCount();

                processTimedOut = this.processTimedOutEventWithRetries();

                if (this.totalRetriesReached()) {
                    this.setProcessInformedOfTimeout(true);
                } else {
                    this.setTimeoutOccursOn(
                            this.timeoutOccursOn()
                                    .plus(this.allowableDuration()));
                }
            }

            DomainEventPublisher.instance().publish(processTimedOut);
        }
    }

    public int retryCount() {
        return this.retryCount;
    }

    public String tenantId() {
        return this.tenantId;
    }

    public long timeConstrainedProcessTrackerId() {
        return this.timeConstrainedProcessTrackerId;
    }

    public Instant timeoutOccursOn() {
        return this.timeoutOccursOn;
    }

    public int totalRetriesPermitted() {
        return this.totalRetriesPermitted;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TimeConstrainedProcessTracker that = (TimeConstrainedProcessTracker) o;
        return processId.equals(that.processId) &&
                tenantId.equals(that.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processId, tenantId);
    }

    @Override
    public String toString() {
        return "TimeConstrainedProcessTracker{" +
                "allowableDuration=" + allowableDuration +
                ", completed=" + completed +
                ", description='" + description + '\'' +
                ", processId=" + processId +
                ", processInformedOfTimeout=" + processInformedOfTimeout +
                ", processTimedOutEventType='" + processTimedOutEventType + '\'' +
                ", retryCount=" + retryCount +
                ", tenantId='" + tenantId + '\'' +
                ", timeConstrainedProcessTrackerId=" + timeConstrainedProcessTrackerId +
                ", timeoutOccursOn=" + timeoutOccursOn +
                ", totalRetriesPermitted=" + totalRetriesPermitted +
                '}';
    }

    protected TimeConstrainedProcessTracker() {
        super();
    }

    protected int concurrencyVersion() {
        return this.concurrencyVersion;
    }

    protected void setConcurrencyVersion(final int version) {
        this.failWhenConcurrencyViolation(version);
        this.concurrencyVersion = version;
    }

    private void incrementRetryCount() {
        this.retryCount++;
    }

    private void setAllowableDuration(final Duration allowableDuration) {
        this.assertArgumentFalse(
                allowableDuration.isZero() || allowableDuration.isNegative(),
                "The allowable duration must be greater than zero.");

        this.allowableDuration = allowableDuration;
    }

    private void setDescription(final String description) {
        this.assertArgumentNotEmpty(description, "Description is required.");
        this.assertArgumentLength(description, 1, 100, "Description must be 1 to 100 characters in length.");

        this.description = description;
    }

    private void setProcessInformedOfTimeout(final boolean isProcessInformedOfTimeout) {
        this.processInformedOfTimeout = isProcessInformedOfTimeout;
    }

    private ProcessTimedOut processTimedOutEvent() {
        final ProcessTimedOut processTimedOut;

        try {
            final Class<?> processTimedOutClass =
                    Class.forName(this.processTimedOutEventType());

            final Constructor<?> constructor = processTimedOutClass.getConstructor(String.class, ProcessId.class);

            processTimedOut = (ProcessTimedOut) constructor.newInstance(this.tenantId(), this.processId());

        } catch (Exception e) {
            throw new IllegalStateException("Unable to create new ProcessTimedOut instance.", e);
        }

        return processTimedOut;
    }

    private ProcessTimedOut processTimedOutEventWithRetries() {
        final ProcessTimedOut processTimedOut;

        try {
            final Class<?> processTimedOutClass =
                    Class.forName(this.processTimedOutEventType());

            final Constructor<?> constructor =
                    processTimedOutClass
                            .getConstructor(
                                    String.class,
                                    ProcessId.class,
                                    int.class,
                                    int.class);

            processTimedOut = (ProcessTimedOut)
                    constructor.newInstance(
                            this.tenantId(),
                            this.processId(),
                            this.totalRetriesPermitted(),
                            this.retryCount());

        } catch (Exception e) {
            throw new IllegalStateException("Unable to create new ProcessTimedOut instance.", e);
        }

        return processTimedOut;
    }

    private void setProcessId(final ProcessId processId) {
        this.assertArgumentNotNull(processId, "ProcessId is required.");

        this.processId = processId;
    }

    private void setProcessTimedOutEventType(final String processTimedOutEventType) {
        this.assertArgumentNotEmpty(processTimedOutEventType, "ProcessTimedOutEventType is required.");

        this.processTimedOutEventType = processTimedOutEventType;
    }

    private void setTenantId(final String tenantId) {
        this.assertArgumentNotEmpty(tenantId, "TenantId is required.");

        this.tenantId = tenantId;
    }

    private void setTimeConstrainedProcessTrackerId(final long timeConstrainedProcessTrackerId) {
        this.timeConstrainedProcessTrackerId = timeConstrainedProcessTrackerId;
    }

    private void setTimeoutOccursOn(final Instant timeoutOccursOn) {
        this.assertArgumentNotNull(timeoutOccursOn, "Timeout must be provided.");

        this.timeoutOccursOn = timeoutOccursOn;
    }

    private void setTotalRetriesPermitted(final int totalRetriesPermitted) {
        this.assertArgumentTrue(
                totalRetriesPermitted >= 0,
                "Total retries must be greater than or equal to zero.");

        this.totalRetriesPermitted = totalRetriesPermitted;
    }

    private boolean totalRetriesReached() {
        return this.retryCount() >= this.totalRetriesPermitted();
    }
}
