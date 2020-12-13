package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.AssertionConcern;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public final class Enablement extends AssertionConcern implements Serializable {

    private boolean enabled;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static Enablement indefiniteEnablement() {
        return new Enablement(true, null, null);
    }

    public Enablement(
            final boolean enabled,
            final LocalDateTime startDate,
            final LocalDateTime endDate) {

        if (startDate != null || endDate != null) {
            this.assertArgumentNotNull(startDate, "Start date must be provided.");
            this.assertArgumentNotNull(endDate, "End date must be provided.");
            this.assertArgumentFalse(startDate.isAfter(endDate), "Enablement start and/or end date is invalid.");
        }

        this.setEnabled(enabled);
        this.setEndDate(endDate);
        this.setStartDate(startDate);
    }

    public Enablement(final Enablement enablement) {
        this(enablement.isEnabled(), enablement.startDate(), enablement.endDate());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isEnablementEnabled() {
        return this.isEnabled() && !this.isTimeExpired();
    }

    public LocalDateTime endDate() {
        return this.endDate;
    }

    public boolean isTimeExpired() {

        if (this.startDate() != null && this.endDate() != null) {
            final LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(this.startDate()) || now.isAfter(this.endDate())) {
                return true;
            }
        }

        return false;
    }

    public LocalDateTime startDate() {
        return this.startDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Enablement that = (Enablement) o;
        return enabled == that.enabled && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, startDate, endDate);
    }

    @Override
    public String toString() {
        return "Enablement{" +
                "enabled=" + enabled +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

    protected Enablement() {
        // Needed by Hibernate
    }

    private void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    private void setEndDate(final LocalDateTime endDate) {
        this.endDate = endDate;
    }

    private void setStartDate(final LocalDateTime startDate) {
        this.startDate = startDate;
    }
}
