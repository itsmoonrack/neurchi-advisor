package com.neurchi.advisor.common.domain.model.process;

import com.neurchi.advisor.common.domain.model.Entity;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public abstract class AbstractProcess extends Entity implements Process {

    private Duration allowableDuration;
    private int concurrencyVersion;
    private String description;
    private ProcessId processId;
    private ProcessCompletionType processCompletionType;
    private Instant startTime;
    private String tenantId;
    private Instant timedOutDate;
    private int totalRetriesPermitted;

    public AbstractProcess(
            final String tenantId,
            final ProcessId processId,
            final String description) {

        this.setDescription(description);
        this.setProcessCompletionType(ProcessCompletionType.NotCompleted);
        this.setProcessId(processId);
        this.setStartTime(Instant.now());
        this.setTenantId(tenantId);
    }

    public AbstractProcess(
            final String tenantId,
            final ProcessId processId,
            final String description,
            final Duration allowableDuration) {

        this(tenantId, processId, description);

        this.setAllowableDuration(allowableDuration);
    }

    public AbstractProcess(
            final String tenantId,
            final ProcessId processId,
            final String description,
            final Duration allowableDuration,
            final int totalRetriesPermitted) {

        this(tenantId, processId, description, allowableDuration);

        this.setTotalRetriesPermitted(totalRetriesPermitted);
    }

    @Override
    public Duration allowableDuration() {
        return this.allowableDuration;
    }

    @Override
    public boolean canTimeout() {
        return this.allowableDuration() != null && !this.allowableDuration().isNegative();
    }

    @Override
    public Duration currentDuration() {
        return this.calculateTotalCurrentDuration(Instant.now());
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public boolean didProcessingComplete() {
        return this.isCompleted() && !this.isTimedOut();
    }

    public void failWhenConcurrencyViolation(final int version) {
        this.assertStateTrue(
                version == this.concurrencyVersion(),
                "Concurrency Violation: Stale data detected. Entity was already modified.");
    }

    @Override
    public void informTimeout(final Instant timedOutDate) {
        this.assertStateTrue(
                this.hasProcessTimedOut(timedOutDate),
                "The date " + timedOutDate + " does not indicate a valid timeout.");

        this.setProcessCompletionType(ProcessCompletionType.TimedOut);
        this.setTimedOutDate(timedOutDate);
    }

    @Override
    public boolean isCompleted() {
        return !this.notCompleted();
    }

    @Override
    public boolean isTimedOut() {
        return this.timedOutDate() != null;
    }

    @Override
    public boolean notCompleted() {
        return this.processCompletionType().equals(ProcessCompletionType.NotCompleted);
    }

    @Override
    public ProcessCompletionType processCompletionType() {
        return this.processCompletionType;
    }

    @Override
    public ProcessId processId() {
        return this.processId;
    }

    @Override
    public Instant startTime() {
        return this.startTime;
    }

    public String tenantId() {
        return this.tenantId;
    }

    @Override
    public TimeConstrainedProcessTracker timeConstrainedProcessTracker() {
        this.assertStateTrue(this.canTimeout(), "Process does not timeout.");

        return new TimeConstrainedProcessTracker(
                this.tenantId(),
                this.processId(),
                this.description(),
                this.startTime(),
                this.allowableDuration(),
                this.totalRetriesPermitted(),
                this.processTimedOutEventType().getName());
    }

    @Override
    public Instant timedOutDate() {
        return this.timedOutDate;
    }

    @Override
    public Duration totalAllowableDuration() {
        final Duration totalAllowableDuration = this.allowableDuration();
        final long totalRetriesPermitted = this.totalRetriesPermitted();

        if (totalRetriesPermitted > 0) {
            return totalAllowableDuration.multipliedBy(totalRetriesPermitted);
        }

        return totalAllowableDuration;
    }

    @Override
    public int totalRetriesPermitted() {
        return this.totalRetriesPermitted;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AbstractProcess that = (AbstractProcess) o;
        return processId.equals(that.processId) &&
                tenantId.equals(that.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processId, tenantId);
    }

    @Override
    public String toString() {
        return "AbstractProcess{" +
                "id=" + id() +
                "allowableDuration=" + allowableDuration +
                ", description='" + description + '\'' +
                ", processId=" + processId +
                ", processCompletionType=" + processCompletionType +
                ", startTime=" + startTime +
                ", tenantId='" + tenantId + '\'' +
                ", timedOutDate=" + timedOutDate +
                ", totalRetriesPermitted=" + totalRetriesPermitted +
                '}';
    }

    protected AbstractProcess() {
        super();
    }

    protected void completeProcess(final ProcessCompletionType processCompletionType) {
        if (!this.isCompleted() && this.completenessVerified()) {
            this.setProcessCompletionType(processCompletionType);
        }
    }

    protected int concurrencyVersion() {
        return this.concurrencyVersion;
    }

    protected void setConcurrencyVersion(final int version) {
        this.failWhenConcurrencyViolation(version);
        this.concurrencyVersion = version;
    }

    protected abstract boolean completenessVerified();

    protected abstract Class<? extends ProcessTimedOut> processTimedOutEventType();

    private Duration calculateTotalCurrentDuration(final Instant dateFollowingStartTime) {
        return Duration.between(this.startTime(), dateFollowingStartTime);
    }

    private boolean hasProcessTimedOut(final Instant timedOutDate) {
        return this.calculateTotalCurrentDuration(timedOutDate).compareTo(this.totalAllowableDuration()) >= 0;
    }

    private void setAllowableDuration(final Duration allowableDuration) {
        this.assertArgumentNotNull(
                allowableDuration,
                "The allowable duration must be provided.");
        this.assertArgumentFalse(
                allowableDuration.isZero() || allowableDuration.isNegative(),
                "The allowable duration must be greater than zero.");

        this.allowableDuration = allowableDuration;
    }

    private void setDescription(final String description) {
        this.description = description;
    }

    private void setProcessCompletionType(final ProcessCompletionType processCompletionType) {
        this.processCompletionType = processCompletionType;
    }

    private void setProcessId(final ProcessId processId) {
        this.assertArgumentNotNull(processId, "Process id must be provided.");

        this.processId = processId;
    }

    private void setStartTime(final Instant startTime) {
        this.startTime = startTime;
    }

    private void setTenantId(final String tenantId) {
        this.assertArgumentNotEmpty(tenantId, "Tenant id must be provided.");

        this.tenantId = tenantId;
    }

    private void setTimedOutDate(final Instant timedOutDate) {
        this.timedOutDate = timedOutDate;
    }

    private void setTotalRetriesPermitted(final int totalRetriesPermitted) {
        this.totalRetriesPermitted = totalRetriesPermitted;
    }
}
