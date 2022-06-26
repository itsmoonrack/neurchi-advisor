package com.neurchi.advisor.subscription.port.adapter.event;

import com.neurchi.advisor.common.domain.model.DomainEventPublisher;
import com.neurchi.advisor.common.event.sourcing.DispatchableDomainEvent;
import com.neurchi.advisor.common.event.sourcing.EventDispatcher;
import com.neurchi.advisor.common.event.sourcing.EventNotifiable;
import com.neurchi.advisor.common.port.adapter.persistence.ConnectionProvider;
import com.neurchi.advisor.subscription.port.adapter.persistence.EventStoreProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FollowStoreEventDispatcher implements EventDispatcher, EventNotifiable {

    private DataSource subscriptionDataSource;
    private List<EventDispatcher> registeredDispatchers;
    private long lastDispatchedEventId;

    FollowStoreEventDispatcher(final DataSource dataSource) {

        this.subscriptionDataSource = dataSource;
        this.setRegisteredDispatchers(new ArrayList<>());

        EventStoreProvider
                .instance()
                .eventStore().registerEventNotifiable(this);

        this.setLastDispatchedEventId(this.queryLastDispatchedEventId());

        this.notifyDispatchableEvents();
    }

    @Override
    public void dispatch(final DispatchableDomainEvent dispatchableDomainEvent) {
        DomainEventPublisher.instance().publish(dispatchableDomainEvent.domainEvent());

        for (EventDispatcher eventDispatcher : this.registeredDispatchers()) {
            eventDispatcher.dispatch(dispatchableDomainEvent);
        }
    }

    @Override
    public void registerEventDispatcher(final EventDispatcher eventDispatcher) {
        this.registeredDispatchers().add(eventDispatcher);
    }

    @Override
    public boolean understands(final DispatchableDomainEvent dispatchableDomainEvent) {
        return true;
    }

    @Override
    public void notifyDispatchableEvents() {

        // this could be multi-threaded from here,
        // but is not for simplicity

        // child EventDispatchers should use only
        // ConnectionProvider.connection() and
        // not commit. I will commit and close the
        // connection here

        Connection connection =
                ConnectionProvider
                        .connection(this.subscriptionDataSource());

        try {

            List<DispatchableDomainEvent> undispatchedEvents =
                    EventStoreProvider
                            .instance()
                            .eventStore()
                            .eventsSince(this.lastDispatchedEventId());

            if (!undispatchedEvents.isEmpty()) {

                for (DispatchableDomainEvent event : undispatchedEvents) {
                    this.dispatch(event);
                }

                DispatchableDomainEvent withLastEventId =
                        undispatchedEvents.get(undispatchedEvents.size() - 1);

                long lastDispatchedEventId = withLastEventId.eventId();

                this.setLastDispatchedEventId(lastDispatchedEventId);

                this.saveLastDispatchedEventId(connection, lastDispatchedEventId);
            }

            connection.commit();

        } catch (Throwable t) {
            throw new IllegalStateException("Unable to dispatch events because: " + t.getMessage(), t);
        } finally {
            ConnectionProvider.closeConnection();
        }
    }

    private DataSource subscriptionDataSource() {
        return this.subscriptionDataSource;
    }

    private void setSubscriptionDataSource(final DataSource dataSource) {
        this.subscriptionDataSource = dataSource;
    }

    private Connection connection() {
        Connection connection = null;

        try {
            connection =
                    ConnectionProvider
                            .connection(this.subscriptionDataSource());
        } catch (Throwable t) {
            throw new IllegalStateException("Unable to acquire database connection: " + t.getMessage(), t);
        }

        return connection;
    }

    private long lastDispatchedEventId() {
        return this.lastDispatchedEventId;
    }

    private void setLastDispatchedEventId(final long lastDispatchedEventId) {
        this.lastDispatchedEventId = lastDispatchedEventId;
    }

    private long queryLastDispatchedEventId() {

        long lastHandledEventId = 0;

        Connection connection = this.connection();

        try (
                PreparedStatement statement = connection.prepareStatement(
                "select max(event_id) from table_dispatcher_last_event");
                ResultSet result = statement.executeQuery()) {

            if (result.next()) {
                lastHandledEventId = result.getLong(1);
            } else {
                this.saveLastDispatchedEventId(connection, 0);
            }

            connection.commit();

        } catch (Exception e) {
            throw new IllegalStateException("Unable to query last dispatched event: " + e.getMessage(), e);
        } finally {
            ConnectionProvider.closeConnection();
        }

        return lastHandledEventId;
    }

    private void saveLastDispatchedEventId(
            final Connection connection,
            final long lastDispatchedEventId) {

        int updated = 0;

        try (PreparedStatement statement = connection.prepareStatement(
                "update table_dispatcher_last_event set event_id = ?")){

            statement.setLong(1, lastDispatchedEventId);
            updated = statement.executeUpdate();

        } catch (Exception e) {
            throw new IllegalStateException("Unable to update dispatcher last event.", e);
        }

        if (updated == 0) {

            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into table_dispatcher_last_event values(?)")){

                statement.setLong(1, lastDispatchedEventId);
                statement.executeUpdate();

            } catch (Exception e) {
                throw new IllegalStateException("Unable to insert dispatcher last event.", e);
            }
        }
    }

    private List<EventDispatcher> registeredDispatchers() {
        return this.registeredDispatchers;
    }

    private void setRegisteredDispatchers(final List<EventDispatcher> dispatchers) {
        this.registeredDispatchers = dispatchers;
    }
}
