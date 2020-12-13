package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.AssertionConcern;

import java.util.Objects;

public final class FullName extends AssertionConcern {

    private String firstName;
    private String lastName;

    public FullName(final String firstName, final String lastName) {
        this.setFirstName(firstName);
        this.setLastName(lastName);
    }

    public String asFormattedName() {
        return firstName.trim() + " " + lastName.trim();
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public FullName withChangedFirstName(String firstName) {
        return new FullName(firstName, this.lastName());
    }

    public FullName withChangedLastName(String lastName) {
        return new FullName(this.firstName(), lastName);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final FullName fullName = (FullName) o;
        return firstName.equals(fullName.firstName) &&
                lastName.equals(fullName.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }

    @Override
    public String toString() {
        return this.asFormattedName();
    }

    protected FullName() {
        // Needed by Hibernate
    }

    private void setFirstName(final String firstName) {
        this.assertArgumentNotEmpty(firstName, "First name is required.");
        this.assertArgumentLength(firstName, 1, 50, "First name must be 50 characters or less.");

        this.firstName = firstName;
    }

    private void setLastName(final String lastName) {
        this.assertArgumentNotEmpty(lastName, "The last name is required.");
        this.assertArgumentLength(lastName, 1, 50, "The last name must be 50 characters or less.");

        this.lastName = lastName;
    }
}
