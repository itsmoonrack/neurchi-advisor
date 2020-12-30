package com.neurchi.advisor.identityaccess.application;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.common.domain.model.DomainEventPublisher;
import com.neurchi.advisor.common.domain.model.DomainEventSubscriber;
import com.neurchi.advisor.common.event.EventStore;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class IdentityAccessEventProcessor {

    private final EventStore eventStore;

    IdentityAccessEventProcessor(final EventStore eventStore) {
        this.eventStore = eventStore;
    }

    /**
     * Listens for all domain events and stores them.
     */
    @Before("execution(* com.neurchi.advisor.identityaccess.application.*.*(..))")
    public void listen() {
        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<DomainEvent>() {
                    @Override
                    public void handleEvent(final DomainEvent domainEvent) {
                        store(domainEvent);
                    }

                    @Override
                    public Class<DomainEvent> subscribedToType() {
                        return DomainEvent.class; // all domain events
                    }
                });
    }

    @After("execution(* com.neurchi.advisor.identityaccess.application.*.*(..))")
    public void reset() {
        DomainEventPublisher
                .instance()
                .reset();
    }

    private void store(final DomainEvent domainEvent) {
        this.eventStore().append(domainEvent);
    }

    private EventStore eventStore() {
        return this.eventStore;
    }
}
