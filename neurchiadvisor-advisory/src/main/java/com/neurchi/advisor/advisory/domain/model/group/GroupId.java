package com.neurchi.advisor.advisory.domain.model.group;

import com.neurchi.advisor.common.domain.model.AbstractId;

public final class GroupId extends AbstractId {

    public GroupId(final String id) {
        super(id);
    }

    protected GroupId() {
        // Needed by Hibernate.
    }
}
