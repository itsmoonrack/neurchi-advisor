package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.AbstractId;

import java.util.UUID;

public final class TenantId extends AbstractId {

    public TenantId(final String id) {
        super(id);
    }

    protected TenantId() {
        // Needed by Hibernate.
    }

    @Override
    protected void validateId(final String id) {
        try {
            UUID.fromString(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("The is has an invalid format.");
        }
    }
}
