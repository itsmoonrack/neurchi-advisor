package com.neurchi.advisor.subscription.port.adapter.persistence.view;

import com.neurchi.advisor.common.event.sourcing.DispatchableDomainEvent;
import com.neurchi.advisor.common.event.sourcing.EventDispatcher;
import com.neurchi.advisor.common.port.adapter.persistence.AbstractProjection;
import com.neurchi.advisor.subscription.domain.model.group.GroupAdminWithhold;
import com.neurchi.advisor.subscription.domain.model.group.GroupAdministered;

public class MySQLGroupProjection extends AbstractProjection implements EventDispatcher {

    MySQLGroupProjection(final EventDispatcher parentEventDispatcher) {

        understands(GroupAdministered.class, this::when);
        understands(GroupAdminWithhold.class, this::when);

        parentEventDispatcher.registerEventDispatcher(this);
    }

    @Override
    public void dispatch(final DispatchableDomainEvent dispatchableDomainEvent) {
        this.projectWhen(dispatchableDomainEvent);
    }

    @Override
    public void registerEventDispatcher(final EventDispatcher eventDispatcher) {
        throw new UnsupportedOperationException("Cannot register additional dispatchers.");
    }

    @Override
    public boolean understands(final DispatchableDomainEvent dispatchableDomainEvent) {
        return this.understandsAnyOf(dispatchableDomainEvent.domainEvent().getClass());
    }

    protected void when(final GroupAdministered event) {

    }

    protected void when(final GroupAdminWithhold event) {

    }
}
