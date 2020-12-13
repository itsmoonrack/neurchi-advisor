package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.AssertionConcern;

import java.time.LocalDateTime;
import java.util.Objects;

public final class InvitationDescriptor extends AssertionConcern {

    private final String description;
    private final String invitationId;
    private final LocalDateTime startingOn;
    private final TenantId tenantId;
    private final LocalDateTime until;

    public InvitationDescriptor(
            final TenantId tenantId,
            final String invitationId,
            final String description,
            final LocalDateTime startingOn,
            final LocalDateTime until) {

        this.assertArgumentNotNull(tenantId, "The tenantId is required.");
        this.assertArgumentNotEmpty(invitationId, "The invitationId is required.");
        this.assertArgumentNotEmpty(description, "The invitation description is required.");

        this.description = description;
        this.invitationId = invitationId;
        this.startingOn = startingOn;
        this.tenantId = tenantId;
        this.until = until;
    }

    public String description() {
        return description;
    }

    public String invitationId() {
        return invitationId;
    }

    public LocalDateTime startingOn() {
        return startingOn;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public LocalDateTime until() {
        return until;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final InvitationDescriptor that = (InvitationDescriptor) o;

        return Objects.equals(tenantId, that.tenantId)
                && Objects.equals(invitationId, that.invitationId)
                && Objects.equals(description, that.description)
                && Objects.equals(startingOn, that.startingOn)
                && Objects.equals(until, that.until);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, invitationId, description, startingOn, until);
    }
}
