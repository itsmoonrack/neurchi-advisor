package com.neurchi.advisor.common.event;

import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.time.Instant;

public class AnotherTestableDomainEvent implements DomainEvent {

    private int eventVersion;
    private Instant occurredOn;
    private double value;

    public AnotherTestableDomainEvent(final double value) {
        this.eventVersion = 1;
        this.occurredOn = Instant.now();
        this.value = value;
    }

    @Override
    public int eventVersion() {
        return this.eventVersion;
    }

    @Override
    public Instant occurredOn() {
        return this.occurredOn;
    }

    public double value() {
        return this.value;
    }
}
