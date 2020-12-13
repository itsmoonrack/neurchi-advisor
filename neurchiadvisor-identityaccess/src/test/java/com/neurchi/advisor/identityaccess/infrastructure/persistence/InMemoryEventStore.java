//   Copyright 2012,2013 Vaughn Vernon
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.neurchi.advisor.identityaccess.infrastructure.persistence;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.common.event.EventSerializer;
import com.neurchi.advisor.common.event.EventStore;
import com.neurchi.advisor.common.event.StoredEvent;
import com.neurchi.advisor.common.persistence.CleanableStore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class InMemoryEventStore implements EventStore, CleanableStore {

    private List<StoredEvent> storedEvents;

    public InMemoryEventStore() {
        super();

        this.storedEvents = new ArrayList<>();
    }

    @Override
    public Stream<StoredEvent> allStoredEventsBetween(final long lowStoredEventId, final long highStoredEventId) {
        return this.storedEvents.stream().filter(storedEvent -> storedEvent.eventId() >= lowStoredEventId && storedEvent.eventId() <= highStoredEventId);
    }

    @Override
    public Stream<StoredEvent> allStoredEventsSince(final long storedEventId) {
        return this.storedEvents.stream().filter(storedEvent -> storedEvent.eventId() > storedEventId);
    }

    @Override
    public synchronized StoredEvent append(final DomainEvent domainEvent) {
        String eventSerialization =
                EventSerializer.instance().serialize(domainEvent);

        StoredEvent storedEvent =
                new StoredEvent(
                        domainEvent.getClass().getName(),
                        domainEvent.occurredOn(),
                        eventSerialization,
                        this.storedEvents.size() + 1);

        this.storedEvents.add(storedEvent);

        return storedEvent;
    }

    @Override
    public void close() {
        this.clean();
    }

    @Override
    public long countStoredEvents() {
        return this.storedEvents.size();
    }

    @Override
    public void clean() {
        this.storedEvents = new ArrayList<StoredEvent>();
    }
}
