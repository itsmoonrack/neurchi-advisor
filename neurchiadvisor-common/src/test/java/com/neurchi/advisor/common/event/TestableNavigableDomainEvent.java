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

package com.neurchi.advisor.common.event;

import com.neurchi.advisor.common.domain.model.DomainEvent;

import java.time.Instant;

public class TestableNavigableDomainEvent implements DomainEvent {

    private int eventVersion;
    private TestableDomainEvent nestedEvent;
    private Instant occurredOn;

    public TestableNavigableDomainEvent(final long id, final String name) {
        super();

        this.eventVersion  = 1;
        this.nestedEvent = new TestableDomainEvent(id, name);
        this.occurredOn = Instant.now();
    }

    public int eventVersion() {
        return eventVersion;
    }

    public TestableDomainEvent nestedEvent() {
        return nestedEvent;
    }

    @Override
    public Instant occurredOn() {
        return this.occurredOn;
    }
}
