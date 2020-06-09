package com.neurchi.advisor.common.domain.model;

public interface DomainEventSubscriber<T> {

    void handleEvent(final T domainEvent);

    Class<T> subscribedToType();
}
