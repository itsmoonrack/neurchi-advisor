package com.neurchi.advisor.advisory.domain.model.group;

import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.time.Instant;

public class GroupCreated implements DomainEvent {
    @Override
    public int eventVersion() {
        return 0;
    }

    @Override
    public Instant occurredOn() {
        return null;
    }
}
