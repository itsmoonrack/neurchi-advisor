package com.neurchi.advisor.common.event;

import com.neurchi.advisor.common.domain.model.DomainEventPublisher;
import com.neurchi.advisor.common.domain.model.DomainEventSubscriber;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainEventPublisherTest {

    private boolean anotherEventHandled;
    private boolean eventHandled;

    @Test
    public void TestPublish() throws Exception {
        DomainEventPublisher.instance().reset();

        DomainEventPublisher.instance().subscribe(new DomainEventSubscriber<TestableDomainEvent>() {
            @Override
            public void handleEvent(final TestableDomainEvent domainEvent) {
                assertEquals(100L, domainEvent.id());
                assertEquals("test", domainEvent.name());
                eventHandled = true;
            }

            @Override
            public Class<TestableDomainEvent> subscribedToType() {
                return TestableDomainEvent.class;
            }
        });

        assertFalse(this.eventHandled);

        DomainEventPublisher.instance().publish(new TestableDomainEvent(100L, "test"));

        assertTrue(this.eventHandled);
    }

    @Test
    public void TestPublishBlocked() throws Exception {
        DomainEventPublisher.instance().reset();

        DomainEventPublisher.instance().subscribe(new DomainEventSubscriber<TestableDomainEvent>() {
            @Override
            public void handleEvent(final TestableDomainEvent domainEvent) {
                assertEquals(100L, domainEvent.id());
                assertEquals("test", domainEvent.name());
                eventHandled = true;
                // attempt nested publish, which should not work
                DomainEventPublisher.instance().publish(new AnotherTestableDomainEvent(1000.0));
            }

            @Override
            public Class<TestableDomainEvent> subscribedToType() {
                return TestableDomainEvent.class;
            }
        });

        DomainEventPublisher.instance().subscribe(new DomainEventSubscriber<AnotherTestableDomainEvent>() {
            @Override
            public void handleEvent(final AnotherTestableDomainEvent domainEvent) {
                // should never be reached due to blocked publisher
                assertEquals(1000.0, domainEvent.value());
                anotherEventHandled = true;
            }

            @Override
            public Class<AnotherTestableDomainEvent> subscribedToType() {
                return AnotherTestableDomainEvent.class;
            }
        });

        assertFalse(this.eventHandled);
        assertFalse(this.anotherEventHandled);

        DomainEventPublisher.instance().publish(new TestableDomainEvent(100L, "test"));

        assertTrue(this.eventHandled);
        assertFalse(this.anotherEventHandled);
    }
}
