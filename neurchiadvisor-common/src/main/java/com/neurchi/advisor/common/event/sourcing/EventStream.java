package com.neurchi.advisor.common.event.sourcing;

import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.util.stream.Stream;

public interface EventStream {

    Stream<DomainEvent> events();

    int version();

}
