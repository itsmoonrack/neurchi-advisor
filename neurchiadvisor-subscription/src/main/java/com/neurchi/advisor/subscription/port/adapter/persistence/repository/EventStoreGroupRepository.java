package com.neurchi.advisor.subscription.port.adapter.persistence.repository;

import com.neurchi.advisor.common.event.sourcing.EventStore;
import com.neurchi.advisor.common.event.sourcing.EventStoreVersionException;
import com.neurchi.advisor.common.event.sourcing.EventStream;
import com.neurchi.advisor.common.event.sourcing.EventStreamId;
import com.neurchi.advisor.subscription.domain.model.group.Group;
import com.neurchi.advisor.subscription.domain.model.group.GroupId;
import com.neurchi.advisor.subscription.domain.model.group.GroupRepository;
import com.neurchi.advisor.subscription.domain.model.tenant.Tenant;
import org.springframework.stereotype.Repository;

@Repository
public class EventStoreGroupRepository implements GroupRepository {

    private final EventStore eventStore;

    EventStoreGroupRepository(final EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public Group groupOfId(final Tenant tenant, final GroupId groupId) {

        try {
            // snapshots not currently supported; always use version 1

            EventStreamId identity = new EventStreamId(tenant.id(), groupId.id());

            EventStream eventStream = this.eventStore().eventStreamSince(identity);

            Group group = new Group(eventStream.events(), eventStream.version());

            return group;

        } catch (EventStoreVersionException e) {
            return null;
        }
    }

    @Override
    public void save(final Group group) {

        EventStreamId startingIdentity =
                new EventStreamId(
                        group.tenant().id(),
                        group.groupId().id(),
                        group.mutatedVersion());

        this.eventStore().appendWith(startingIdentity, group.mutatingEvents());
    }

    private EventStore eventStore() {
        return this.eventStore;
    }
}
