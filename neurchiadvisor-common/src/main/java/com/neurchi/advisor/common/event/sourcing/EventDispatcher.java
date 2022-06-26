package com.neurchi.advisor.common.event.sourcing;

public interface EventDispatcher {

    void dispatch(DispatchableDomainEvent dispatchableDomainEvent);

    void registerEventDispatcher(EventDispatcher eventDispatcher);

    boolean understands(DispatchableDomainEvent dispatchableDomainEvent);
}
