package com.neurchi.advisor.common.port.adapter.persistence.eventsourcing.mysql;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.common.event.EventSerializer;
import com.neurchi.advisor.common.event.sourcing.*;
import com.neurchi.advisor.common.port.adapter.persistence.eventsourcing.DefaultEventStream;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class MySQLJdbcEventStore implements EventStore {

    private DataSource dataSource;
    private EventNotifiable eventNotifiable;
    private EventSerializer serializer;

    public MySQLJdbcEventStore(final DataSource dataSource) {
        this.dataSource = dataSource;
        this.serializer = EventSerializer.instance();
    }

    @Override
    public void appendWith(final EventStreamId startingIdentity, final List<DomainEvent> events) {

        // event_store must have a composite primary key
        // consisting of {stream_name}:{streamVersion} so that
        // appending a stale version will fail the pk constraint

        try (Connection connection = this.connection()) {
            int index = 0;

            for (DomainEvent event : events) {
                this.appendEventStore(connection, startingIdentity, index++, event);
            }

            connection.commit();

            this.notifyDispatchableEvents();

        } catch (Throwable t) {
            throw new EventStoreAppendException("Unable to append to event store: " + t.getMessage(), t);
        }
    }

    private void appendEventStore(final Connection connection, final EventStreamId identity, final int index, final DomainEvent domainEvent) throws Exception {

        try {
            PreparedStatement statement =
                    connection
                            .prepareStatement(
                                    "INSERT INTO table_event_store VALUES(?, ?, ?, ?, ?)");

            statement.setLong(1, 0);
            statement.setString(2, this.serializer().serialize(domainEvent));
            statement.setString(3, domainEvent.getClass().getName());
            statement.setString(4, identity.streamName());
            statement.setInt(5, identity.streamVersion() + index);

            statement.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
        }
    }

    @Override
    public List<DispatchableDomainEvent> eventsSince(final long lastReceivedEvent) {
        return null;
    }

    @Override
    public EventStream eventStreamSince(final EventStreamId identity) {

        try (Connection connection = this.connection()) {

            PreparedStatement statement =
                    connection
                        .prepareStatement(
                                "SELECT stream_version, event_type, event_body FROM table_event_store " +
                                        "WHERE stream_name = ? AND stream_version >= ? " +
                                        "ORDER BY stream_version");

            statement.setString(1, identity.streamName());
            statement.setInt(2, identity.streamVersion());

            try (ResultSet result = statement.executeQuery()) {

                EventStream eventStream = this.buildEventStream(result);

                if (eventStream.version() == 0) {
                    throw new EventStoreVersionException("There is no such event stream: " + identity.streamName() + " : " + identity.streamVersion());
                }

                return eventStream;
            }

        } catch (EventStoreVersionException e) {
            throw e;
        } catch (Throwable t) {
            throw new EventStoreException("Unable to query event stream for: " + identity.streamName() + " since version: " + identity.streamVersion(), t);
        }
    }

    @Override
    public EventStream fullEventStreamFor(final EventStreamId identity) {
        return null;
    }

    @Override
    public void registerEventNotifiable(final EventNotifiable eventNotifiable) {

    }

    @Override
    public void close() throws Exception {
        // no-op
    }

    @SuppressWarnings("unchecked")
    private EventStream buildEventStream(final ResultSet resultSet) throws Exception {

        List<DomainEvent> events = new ArrayList<>();

        int version = 0;

        while (resultSet.next()) {
            version = resultSet.getInt("stream_version");

            String eventClassName = resultSet.getString("event_type");

            InputStream eventBody = resultSet.getBinaryStream("event_body");

            Class<DomainEvent> eventClass = (Class<DomainEvent>) Class.forName(eventClassName);

            DomainEvent domainEvent = this.serializer().deserialize(eventBody, eventClass);

            events.add(domainEvent);
        }

        return new DefaultEventStream(events, version);
    }

    private void notifyDispatchableEvents() {
        EventNotifiable eventNotifiable = this.eventNotifiable();

        if (eventNotifiable != null) {
            eventNotifiable.notifyDispatchableEvents();
        }
    }

    private Connection connection() {
        try {
            return this.dataSource().getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot acquire database connection.");
        }
    }

    private DataSource dataSource() {
        return this.dataSource;
    }

    private EventSerializer serializer() {
        return this.serializer;
    }

    private EventNotifiable eventNotifiable() {
        return this.eventNotifiable;
    }
}
