package com.neurchi.advisor.advisory.domain.model.subscription;

import com.neurchi.advisor.common.domain.model.AbstractId;

public final class SubscriptionDescriptor extends AbstractId {

    private static final String UNDEFINED_ID = "undefined";

    public SubscriptionDescriptor(final String id) {
        super(id);
    }

    public SubscriptionDescriptor() {
        super(UNDEFINED_ID);
    }

    public boolean isUndefined() {
        return this.id().equals(UNDEFINED_ID);
    }
}
