package com.neurchi.advisor.advisory.domain.model.tenant;

import com.neurchi.advisor.common.domain.model.AbstractId;

public final class TenantId extends AbstractId {

    public TenantId(final String id) {
        super(id);
    }

    protected TenantId() {
        // Needed by Hibernate.
    }
}
