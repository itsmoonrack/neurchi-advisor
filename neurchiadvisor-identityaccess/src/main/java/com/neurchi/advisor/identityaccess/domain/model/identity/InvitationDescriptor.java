package com.neurchi.advisor.identityaccess.domain.model.identity;

import java.time.LocalDateTime;

public record InvitationDescriptor(
        TenantId tenantId,
        String invitationId,
        String description,
        LocalDateTime startingOn,
        LocalDateTime until) {

    public InvitationDescriptor {
        if (tenantId == null) {
            throw new IllegalArgumentException("The tenantId is required.");
        }
        if (invitationId == null || invitationId.isBlank()) {
            throw new IllegalArgumentException("The tenantId is required.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("The invitation description is required.");
        }
    }
}
