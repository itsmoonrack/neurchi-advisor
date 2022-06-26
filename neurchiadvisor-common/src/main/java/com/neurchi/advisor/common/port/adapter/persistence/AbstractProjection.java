package com.neurchi.advisor.common.port.adapter.persistence;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.common.event.sourcing.DispatchableDomainEvent;
import com.neurchi.advisor.common.event.sourcing.EventDispatcher;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractProjection implements EventDispatcher {

    private final Map<Class<? extends DomainEvent>, ProjectionMethod<? extends DomainEvent>> projectionMethods = new HashMap<>();

    protected boolean understandsAnyOf(final Class<? extends DomainEvent> dispatchedType) {
        return projectionMethods.containsKey(dispatchedType);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void projectWhen(final DispatchableDomainEvent dispatchableDomainEvent) {

        DomainEvent domainEvent = dispatchableDomainEvent.domainEvent();

        Class<? extends DomainEvent> eventType = domainEvent.getClass();

        ProjectionMethod projectionMethod = projectionMethods.get(eventType);

        if (projectionMethod == null) {
            return;
        }

        try {
            projectionMethod.when(domainEvent);
        } catch (Exception e) {
            throw new RuntimeException("Unable to project " + eventType.getSimpleName() + ".", e);
        }
    }

    protected <T extends DomainEvent> void understands(final Class<T> eventType, final ProjectionMethod<T> projectionMethod) {
        projectionMethods.put(eventType, projectionMethod);
    }

    protected interface ProjectionMethod<T extends DomainEvent> {
        void when(T domainEvent) throws Exception;
    }
}
