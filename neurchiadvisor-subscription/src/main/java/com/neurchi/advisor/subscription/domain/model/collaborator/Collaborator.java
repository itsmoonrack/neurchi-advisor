package com.neurchi.advisor.subscription.domain.model.collaborator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

import static java.util.Comparator.comparing;

public abstract class Collaborator implements Comparable<Collaborator>, Serializable {

    private String name;
    private String identity;
    private String profilePicture;

    public final static Comparator<Collaborator> comparator =
            comparing(Collaborator::identity)
                    .thenComparing(Collaborator::name)
                    .thenComparing(Collaborator::profilePicture);

    public Collaborator(final String identity, final String name, final String profilePicture) {
        this.name = name;
        this.identity = identity;
        this.profilePicture = profilePicture;
    }

    public String name() {
        return name;
    }

    public String identity() {
        return identity;
    }

    public String profilePicture() {
        return profilePicture;
    }

    @Override
    public int compareTo(Collaborator o) {
        return comparator.compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collaborator that = (Collaborator) o;
        return identity.equals(that.identity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identity);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "{name='" + name + "'" +
                ", identity='" + identity + "'" +
                ", profilePicture='" + profilePicture + "'" +
                '}';
    }
}
