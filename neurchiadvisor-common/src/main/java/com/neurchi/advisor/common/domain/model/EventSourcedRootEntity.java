package com.neurchi.advisor.common.domain.model;

import com.neurchi.advisor.common.AssertionConcern;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

public abstract class EventSourcedRootEntity extends AssertionConcern {

    private static final String MUTATOR_METHOD_NAME = "when";
    private static final ConcurrentMap<String, Method> mutatorMethods = new ConcurrentHashMap<>();

    private List<DomainEvent> mutatingEvents;
    private int unmutatedVersion;

    public int mutatedVersion() {
        return this.unmutatedVersion() + 1;
    }

    public List<DomainEvent> mutatingEvents() {
        return this.mutatingEvents;
    }

    public int unmutatedVersion() {
        return this.unmutatedVersion;
    }

    protected EventSourcedRootEntity(final Stream<DomainEvent> eventStream, final int streamVersion) {
        this();
        eventStream.forEach(this::mutateWhen);
        this.setUnmutatedVersion(streamVersion);
    }

    protected EventSourcedRootEntity() {
        this.setMutatingEvents(new ArrayList<>(2));
    }

    protected void apply(final DomainEvent domainEvent) {

        this.mutatingEvents().add(domainEvent);

        this.mutateWhen(domainEvent);
    }

    private void mutateWhen(final DomainEvent domainEvent) {

        final Class<? extends EventSourcedRootEntity> rootType = this.getClass();

        final Class<? extends DomainEvent> eventType = domainEvent.getClass();

        final String key = rootType.getName() + ":" + eventType.getName();

        final Method mutatorMethod = mutatorMethods.computeIfAbsent(key, k -> this.mutatorMethodFor(rootType, eventType));

        try {
            mutatorMethod.invoke(this, domainEvent);

        } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
                throw new RuntimeException("Method " + MUTATOR_METHOD_NAME + '(' + eventType.getSimpleName() + ") failed.", e.getCause());
            }
            throw new RuntimeException("Method " + MUTATOR_METHOD_NAME + '(' + eventType.getSimpleName() + ") failed.", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Method " + MUTATOR_METHOD_NAME + '(' + eventType.getSimpleName() + ") failed.", e);
        }
    }

    private Method mutatorMethodFor(final Class<? extends EventSourcedRootEntity> rootType, final Class<? extends DomainEvent> eventType) {
        try {
            final Method method = this.hiddenOrPublicMethod(rootType, eventType);
            method.setAccessible(true);
            return method;
        } catch (Exception e) {
            throw new IllegalArgumentException("I do not understand " + MUTATOR_METHOD_NAME + '(' + eventType.getSimpleName() + ")", e);
        }
    }

    private Method hiddenOrPublicMethod(final Class<? extends EventSourcedRootEntity> rootType, final Class<? extends DomainEvent> eventType) throws Exception {
        try {
            // assume protected or private...
            return rootType.getDeclaredMethod(MUTATOR_METHOD_NAME, eventType);
        } catch (Exception e) {
            // then public...
            return rootType.getMethod(MUTATOR_METHOD_NAME, eventType);
        }
    }

    private void setMutatingEvents(final List<DomainEvent> mutatingEvents) {
        this.mutatingEvents = mutatingEvents;
    }

    private void setUnmutatedVersion(final int streamVersion) {
        this.unmutatedVersion = streamVersion;
    }
}
