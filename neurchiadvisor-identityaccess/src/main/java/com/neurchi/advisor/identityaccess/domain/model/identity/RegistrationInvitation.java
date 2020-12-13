package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.ConcurrencySafeEntity;

import java.time.LocalDateTime;
import java.util.Objects;

public class RegistrationInvitation extends ConcurrencySafeEntity {
    
    private String description;
    private String invitationId;
    private LocalDateTime startingOn;
    private TenantId tenantId;
    private LocalDateTime until;

    public String description() {
        return description;
    }

    public String invitationId() {
        return invitationId;
    }

    public boolean isAvailable() {
        if (this.startingOn() == null && this.until() == null) {
            return true;
        } else {
            final LocalDateTime now = LocalDateTime.now();
            return now.isAfter(this.startingOn()) && now.isBefore(this.until());
        }
    }

    public boolean isIdentifiedBy(final String invitationIdentifier) {
        boolean isIdentified = this.invitationId().equals(invitationIdentifier);
        if (!isIdentified && this.description() != null) {
            isIdentified = this.description().equals(invitationIdentifier);
        }
        return isIdentified;
    }

    public RegistrationInvitation openEnded() {
        setStartingOn(null);
        setUntil(null);
        return this;
    }

    public LocalDateTime startingOn() {
        return startingOn;
    }

    public RegistrationInvitation startingOn(final LocalDateTime date) {
        if (this.until() != null) {
            throw new IllegalStateException("Cannot set starting-on date after until date.");
        }

        this.setStartingOn(date);

        // Temporary if until() properly follows, but
        // prevents illegal state if until() doesn't follow.
        this.setUntil(date.plusDays(1));

        return this;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public InvitationDescriptor toDescriptor() {
        return new InvitationDescriptor(
                this.tenantId(),
                this.invitationId(),
                this.description(),
                this.startingOn(),
                this.until());
    }

    public LocalDateTime until() {
        return until;
    }

    public RegistrationInvitation until(final LocalDateTime date) {
        if (this.startingOn() == null) {
            throw new IllegalStateException("Cannot set until date before setting starting-on date.");
        }

        this.setUntil(date);

        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RegistrationInvitation that = (RegistrationInvitation) o;
        return tenantId.equals(that.tenantId) &&
                invitationId.equals(that.invitationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, invitationId);
    }

    protected RegistrationInvitation(final TenantId tenantId,
                                     final String invitationId,
                                     final String description) {

        this.setDescription(description);
        this.setInvitationId(invitationId);
        this.setTenantId(tenantId);

        this.assertValidInvitationDates();
    }

    protected RegistrationInvitation() {
        // Needed by Hibernate.
    }

    protected void assertValidInvitationDates() {
        // Either both dates must be null, or both dates must be set.
        if (this.startingOn() == null && this.until() == null) {
            // Valid.
        } else if (this.startingOn() == null || !Objects.equals(this.startingOn(), this.until())) {
            throw new IllegalStateException("This is an invalid open-ended invitation.");
        } else if (this.startingOn().isAfter(this.until())) {
            throw new IllegalStateException("The starting date and time must be before the until date and time.");
        }
    }

    protected void setDescription(final String description) {
        this.assertArgumentNotEmpty(description, "The invitation description is required.");
        this.assertArgumentLength(description, 1, 100, "The invitation description must be 100 characters or less.");

        this.description = description;
    }

    protected void setInvitationId(final String invitationId) {
        this.assertArgumentNotEmpty(invitationId, "The invitationId is required.");
        this.assertArgumentLength(invitationId, 1, 36, "The invitation id must be 36 characters or less.");

        this.invitationId = invitationId;
    }

    protected void setStartingOn(final LocalDateTime startingOn) {
        this.startingOn = startingOn;
    }

    protected void setTenantId(final TenantId tenantId) {
        this.assertArgumentNotNull(tenantId, "The tenantId is required.");

        this.tenantId = tenantId;
    }

    protected void setUntil(final LocalDateTime until) {
        this.until = until;
    }
}
