package com.neurchi.advisor.common.notification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.neurchi.advisor.common.media.AbstractJSONMediaReader;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;

import java.util.*;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class NotificationLogReader
        extends AbstractJSONMediaReader
        implements List<NotificationReader>, Iterable<NotificationReader> {

    private ArrayNode array;
    private Map<LinkRelation, Link> links;

    public NotificationLogReader(final String jsonRepresentation, final List<String> links) {
        super(jsonRepresentation);

        this.array = this.array("notifications");

        if (this.array == null) {
            this.array = JsonNodeFactory.instance.arrayNode();
        }

        if (!this.array.isArray()) {
            throw new IllegalStateException("There are no notifications, and the representation may not be a log.");
        }

        if (links == null) {
            this.links = new HashMap<>();
        } else {
            this.links = links.stream().map(Link::valueOf).collect(toMap(Link::getRel, identity()));
        }
    }

    public boolean isArchived() {
        return this.booleanValue("archived");
    }

    public String id() {
        return this.stringValue("id");
    }

    public Iterator<NotificationReader> notifications() {
        return this.iterator();
    }

    public boolean hasNext() {
        return this.links.containsKey(IanaLinkRelations.NEXT);
    }

    public Link next() {
        return this.links.get(IanaLinkRelations.NEXT);
    }

    public boolean hasPrevious() {
        return this.links.containsKey(IanaLinkRelations.PREVIOUS);
    }

    public Link previous() {
        return this.links.get(IanaLinkRelations.PREVIOUS);
    }

    public boolean hasSelf() {
        return this.links.containsKey(IanaLinkRelations.SELF);
    }

    public Link self() {
        return this.links.get(IanaLinkRelations.SELF);
    }

    ///////////////////////////////////////////////
    // Iterable and Collection implementations
    ///////////////////////////////////////////////

    @Override
    public Iterator<NotificationReader> iterator() {
        return new NotificationReaderIterator();
    }

    @Override
    public int size() {
        return this.array.size();
    }

    @Override
    public boolean isEmpty() {
        return this.array.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        throw new UnsupportedOperationException("Cannot ask contains.");
    }

    @Override
    public Object[] toArray() {

        List<NotificationReader> readers = new ArrayList<>(this);

        return readers.toArray();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(final T[] a) {
        return (T[]) this.toArray();
    }

    @Override
    public boolean add(final NotificationReader notificationReader) {
        throw new UnsupportedOperationException("Cannot add.");
    }

    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException("Cannot remove.");
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        throw new UnsupportedOperationException("Cannot ask containsAll.");
    }

    @Override
    public boolean addAll(final Collection<? extends NotificationReader> c) {
        throw new UnsupportedOperationException("Cannot addAll.");
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends NotificationReader> c) {
        throw new UnsupportedOperationException("Cannot addAll.");
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException("Cannot removeAll.");
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException("Cannot retainAll.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Cannot clear.");
    }

    @Override
    public NotificationReader get(final int index) {
        JsonNode element = this.array.get(index);

        return new NotificationReader(element);
    }

    @Override
    public NotificationReader set(final int index, final NotificationReader element) {
        throw new UnsupportedOperationException("Cannot set.");
    }

    @Override
    public void add(final int index, final NotificationReader element) {
        throw new UnsupportedOperationException("Cannot add.");
    }

    @Override
    public NotificationReader remove(final int index) {
        throw new UnsupportedOperationException("Cannot remove.");
    }

    @Override
    public int indexOf(final Object o) {
        throw new UnsupportedOperationException("Cannot ask indexOf.");
    }

    @Override
    public int lastIndexOf(final Object o) {
        throw new UnsupportedOperationException("Cannot ask lastIndexOf.");
    }

    @Override
    public ListIterator<NotificationReader> listIterator() {
        return new NotificationReaderIterator();
    }

    @Override
    public ListIterator<NotificationReader> listIterator(final int index) {
        return new NotificationReaderIterator();
    }

    @Override
    public List<NotificationReader> subList(final int fromIndex, final int toIndex) {

        // this implementation is not a typical subList(), but
        // provides only an immutable subset of the original list

        List<NotificationReader> readers = new ArrayList<>();

        for (int index = fromIndex; index < toIndex; ++index) {
            NotificationReader reader = this.get(index);

            readers.add(reader);
        }

        return Collections.unmodifiableList(readers);
    }

    private class NotificationReaderIterator implements ListIterator<NotificationReader> {

        private int index;

        NotificationReaderIterator() {

        }

        @Override
        public boolean hasNext() {
            return this.nextIndex() < size();
        }

        @Override
        public NotificationReader next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException("No such next element.");
            }

            return get(this.index++);
        }

        @Override
        public boolean hasPrevious() {
            return this.previousIndex() >= 0;
        }

        @Override
        public NotificationReader previous() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException("No such previous element.");
            }

            return get(--this.index);
        }

        @Override
        public int nextIndex() {
            return this.index;
        }

        @Override
        public int previousIndex() {
            return this.index - 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove.");
        }

        @Override
        public void set(final NotificationReader notificationReader) {
            throw new UnsupportedOperationException("Cannot set.");
        }

        @Override
        public void add(final NotificationReader notificationReader) {
            throw new UnsupportedOperationException("Cannot add.");
        }
    }
}
