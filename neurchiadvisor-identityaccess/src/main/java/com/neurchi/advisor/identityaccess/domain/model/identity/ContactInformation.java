package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.AssertionConcern;

import java.util.Objects;

public final class ContactInformation extends AssertionConcern {

    private EmailAddress emailAddress;

    public ContactInformation(final EmailAddress emailAddress) {

        this.setEmailAddress(emailAddress);
    }

    public ContactInformation changeEmailAddress(final EmailAddress emailAddress) {
        return new ContactInformation(emailAddress);
    }

    public EmailAddress emailAddress() {
        return emailAddress;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ContactInformation that = (ContactInformation) o;
        return emailAddress.equals(that.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailAddress);
    }

    protected ContactInformation() {
        // Needed by Hibernate
    }

    private void setEmailAddress(final EmailAddress emailAddress) {
        this.assertArgumentNotNull(emailAddress, "The email address is required.");

        this.emailAddress = emailAddress;
    }
}
