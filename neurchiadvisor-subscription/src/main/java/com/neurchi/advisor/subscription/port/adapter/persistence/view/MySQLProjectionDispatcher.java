package com.neurchi.advisor.subscription.port.adapter.persistence.view;

import com.neurchi.advisor.common.event.sourcing.DispatchableDomainEvent;
import com.neurchi.advisor.common.event.sourcing.EventDispatcher;

import java.util.ArrayList;
import java.util.List;

public class MySQLProjectionDispatcher implements EventDispatcher {

    private List<EventDispatcher> registeredProjections;

    MySQLProjectionDispatcher(final EventDispatcher parentEventDispatcher) {

        parentEventDispatcher.registerEventDispatcher(this);

        this.setRegisteredProjections(new ArrayList<>());
    }

    @Override
    public void dispatch(final DispatchableDomainEvent dispatchableDomainEvent) {
        for (EventDispatcher projection : this.registeredProjections()) {
            projection.dispatch(dispatchableDomainEvent);
        }
    }

    @Override
    public void registerEventDispatcher(final EventDispatcher projection) {
        this.registeredProjections().add(projection);
    }

    @Override
    public boolean understands(final DispatchableDomainEvent dispatchableDomainEvent) {
        return true;
    }

    private List<EventDispatcher> registeredProjections() {
        return this.registeredProjections;
    }

    private void setRegisteredProjections(final List<EventDispatcher> dispatchers) {
        this.registeredProjections = dispatchers;
    }
}
