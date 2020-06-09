package com.neurchi.advisor.common.port.adapter.messaging;

import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.time.Instant;

public abstract class PhoneNumberProcessEvent implements DomainEvent {

    private int eventVersion;
    private Instant occurredOn;
    private String processId;

    public PhoneNumberProcessEvent(final String processId) {

        this.eventVersion = 1;
        this.occurredOn = Instant.now();
        this.processId = processId;
    }

    @Override
    public int eventVersion() {
        return this.eventVersion;
    }

    @Override
    public Instant occurredOn() {
        return this.occurredOn;
    }

    public String processId() {
        return this.processId;
    }
}
